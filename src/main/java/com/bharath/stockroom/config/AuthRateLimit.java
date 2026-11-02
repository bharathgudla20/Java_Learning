package com.bharath.stockroom.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Small, process-local guard for the portfolio's public authentication endpoints. */
@Component
public class AuthRateLimit extends OncePerRequestFilter {
    private record Bucket(long minute,AtomicInteger hits) {}
    private final ConcurrentHashMap<String,Bucket> buckets=new ConcurrentHashMap<>();
    @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
        String path=req.getRequestURI();
        if("POST".equals(req.getMethod()) && (path.equals("/api/auth/demo") || path.equals("/api/auth/login") || path.equals("/api/auth/register"))) {
            long minute=System.currentTimeMillis()/60000;
            if(buckets.size()>5000) buckets.entrySet().removeIf(e->e.getValue().minute()<minute);
            Bucket b=buckets.compute(req.getRemoteAddr(),(key,old)->old==null||old.minute()!=minute?new Bucket(minute,new AtomicInteger()):old);
            if(b.hits().incrementAndGet()>20) {res.setStatus(429);res.setHeader("Retry-After","60");res.setContentType("application/json");res.getWriter().write("{\"detail\":\"Too many attempts. Wait one minute and try again.\"}");return;}
        }
        chain.doFilter(req,res);
    }
}
