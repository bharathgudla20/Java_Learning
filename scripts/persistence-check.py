"""Verify a registered account's data across a service restart.
State contains a generated test password; write it ONLY to a Git-ignored path.
Usage: python scripts/persistence-check.py prepare|check URL .deployment/persistence.json
"""
import http.cookiejar
import json
import pathlib
import secrets
import sys
import urllib.request
import uuid

mode, base, state_path = sys.argv[1:]
state_file = pathlib.Path(state_path)
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
csrf = None

def call(path, method="GET", body=None, headers=None):
    request_headers = dict(headers or {})
    if method != "GET":
        request_headers[csrf["headerName"]] = csrf["token"]
    data = None if body is None else json.dumps(body).encode()
    if data:
        request_headers["Content-Type"] = "application/json"
    with opener.open(urllib.request.Request(base.rstrip("/") + path, data=data, headers=request_headers, method=method), timeout=120) as response:
        text = response.read().decode()
        return json.loads(text) if text else None

csrf = call("/api/auth/csrf")
if mode == "prepare":
    state = {"email": "persistence-" + uuid.uuid4().hex + "@example.com", "password": secrets.token_urlsafe(24)}
    account = call("/api/auth/register", "POST", {**state, "name": "Persistence verification"})
    assert account["ephemeral"] is False and account["demo"] is False
    csrf = call("/api/auth/csrf")
    product = call("/api/products", "POST", {"sku": "PERSIST-" + uuid.uuid4().hex[:8], "name": "Restart verification product", "category": "Verification", "price": "123.45", "stock": 7, "reorderPoint": 2})
    order = call("/api/orders", "POST", {"customer": "Persistence QA", "email": "qa@example.com", "lines": [{"productId": product["id"], "quantity": 2}]}, {"Idempotency-Key": str(uuid.uuid4())})
    state.update(accountId=account["id"], productId=product["id"], orderId=order["id"])
    state_file.parent.mkdir(parents=True, exist_ok=True)
    state_file.write_text(json.dumps(state), encoding="utf-8")
    call("/api/auth/logout", "POST")
    print("PASS: Registered persistent account and saved a product and order. Credentials are in the ignored verification state file.")
elif mode == "check":
    state = json.loads(state_file.read_text(encoding="utf-8"))
    account = call("/api/auth/login", "POST", {"email": state["email"], "password": state["password"]})
    assert account["id"] == state["accountId"] and account["ephemeral"] is False
    products = call("/api/catalog")
    product = next(p for p in products if p["id"] == state["productId"])
    assert product["stock"] == 5 and float(product["price"]) == 123.45
    order = next(o for o in call("/api/orders") if o["id"] == state["orderId"])
    assert order["status"] == "PLACED" and float(order["total"]) == 246.90
    csrf = call("/api/auth/csrf")
    call("/api/auth/logout", "POST")
    print("PASS: After restart, the same registered account, product, stock balance and order are still present in PostgreSQL.")
else:
    raise ValueError("Mode must be prepare or check")
