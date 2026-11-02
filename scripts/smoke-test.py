"""Exercise a running Stockroom server using only Python's standard library.

Creates two disposable DEMO workspaces, never touches a registered workspace.
Usage: python scripts/smoke-test.py http://localhost:8081
"""
import http.cookiejar
import json
import sys
import uuid
import urllib.error
import urllib.request

BASE = (sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8081").rstrip("/")


class Client:
    def __init__(self):
        self.opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
        self.csrf = None

    def request(self, path, method="GET", data=None, headers=None, expect=200):
        headers = dict(headers or {})
        if self.csrf and method != "GET":
            headers[self.csrf["headerName"]] = self.csrf["token"]
        body = None if data is None else json.dumps(data).encode()
        if body is not None:
            headers["Content-Type"] = "application/json"
        req = urllib.request.Request(BASE + path, data=body, headers=headers, method=method)
        try:
            res = self.opener.open(req, timeout=120)
        except urllib.error.HTTPError as error:
            res = error
        text = res.read().decode()
        assert res.code == expect, f"{method} {path}: expected {expect}, got {res.code}: {text[:500]}"
        return json.loads(text) if text else None

    def demo(self):
        self.csrf = self.request("/api/auth/csrf")
        self.request("/api/auth/demo", "POST", expect=200)
        self.csrf = self.request("/api/auth/csrf")


a, b = Client(), Client()
assert a.request("/actuator/health")["status"] == "UP"
a.request("/api/overview", expect=401)
a.request("/api/auth/demo", "POST", expect=403)
a.demo()
b.demo()
before = a.request("/api/overview")
assert before["activeProducts"] == 12 and before["orders"] == 14
product = a.request("/api/products", "POST", {
    "sku": "SMOKE-" + uuid.uuid4().hex[:8], "name": "Smoke test product", "category": "QA",
    "price": "25.50", "stock": 5, "reorderPoint": 2,
}, expect=201)
payload = {"customer": "Disposable QA", "email": "qa@example.com", "lines": [{"productId": product["id"], "quantity": 2}]}
headers = {"Idempotency-Key": str(uuid.uuid4())}
order = a.request("/api/orders", "POST", payload, headers, expect=201)
duplicate = a.request("/api/orders", "POST", payload, headers, expect=201)
assert order["id"] == duplicate["id"] and float(order["total"]) == 51
stock = next(p["stock"] for p in a.request("/api/catalog") if p["id"] == product["id"])
assert stock == 3
b.request(f'/api/products/{product["id"]}/stock', "PATCH", {"change": 1, "reason": "Isolation check"}, expect=404)
a.request(f'/api/orders/{order["id"]}/status', "PATCH", {"status": "CANCELLED"})
a.request(f'/api/orders/{order["id"]}/status', "PATCH", {"status": "CANCELLED"})
assert next(p["stock"] for p in a.request("/api/catalog") if p["id"] == product["id"]) == 5
a.request(f'/api/orders/{order["id"]}/status', "PATCH", {"status": "SHIPPED"}, expect=409)
assert b.request("/api/overview")["orders"] == 14
a.request("/api/demo/reset", "POST", expect=204)
assert a.request("/api/overview")["orders"] == 14
a.request("/api/auth/logout", "POST", expect=204)
a.request("/api/overview", expect=401)
# An old tab can retain a token for an invalidated session. Refresh before logout.
a.request("/api/auth/logout", "POST", expect=403)
a.csrf = a.request("/api/auth/csrf")
a.request("/api/auth/logout", "POST", expect=204)
a.request("/api/auth/me", expect=401)
b.request("/api/auth/logout", "POST", expect=204)
print("PASS: health, authentication, CSRF, demo seeding, product creation, order totals, idempotency, isolation, stock restoration, status rules, reset and logout.")
