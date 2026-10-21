package com.bharath.jobtracker;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.filter.OncePerRequestFilter;

// Basic bounded, per-instance protection. A scaled deployment needs shared rate limiting.
public class AuthRateLimitFilter extends OncePerRequestFilter {
    private final ConcurrentHashMap<String, Window> attempts = new ConcurrentHashMap<>();
    private static final long WINDOW_MS = 5 * 60 * 1000;
    private static class Window {
        final long started = System.currentTimeMillis();
        int count;
        synchronized boolean allow() { return ++count <= 30; }
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                               FilterChain chain) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("POST".equals(request.getMethod()) &&
            (path.equals("/api/auth/login") || path.equals("/api/auth/register"))) {
            long now = System.currentTimeMillis();
            attempts.entrySet().removeIf(entry -> now - entry.getValue().started >= WINDOW_MS);
            String key = request.getRemoteAddr();
            if (!attempts.containsKey(key) && attempts.size() >= 10000) {
                SecurityConfig.json(response, 429, "Please try again in five minutes.");
                return;
            }
            Window window = attempts.computeIfAbsent(key, unused -> new Window());
            if (!window.allow()) {
                response.setHeader("Retry-After", "300");
                SecurityConfig.json(response, 429, "Too many attempts. Try again in five minutes.");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
