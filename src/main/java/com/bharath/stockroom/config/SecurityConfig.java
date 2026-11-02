package com.bharath.stockroom.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.*;

@Configuration
public class SecurityConfig {
    @Bean public org.springframework.security.core.userdetails.UserDetailsService users(com.bharath.stockroom.repository.AccountRepository accounts) {
        return email -> {
            var a=accounts.findByEmail(email.trim().toLowerCase(java.util.Locale.ROOT)).orElseThrow(()->new org.springframework.security.core.userdetails.UsernameNotFoundException("Account not found"));
            return org.springframework.security.core.userdetails.User.withUsername(a.id.toString()).password(a.passwordHash).roles("OWNER").build();
        };
    }
    @Bean public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}
    @Bean public SecurityContextRepository securityContexts() {return new HttpSessionSecurityContextRepository();}
    @Bean public SecurityFilterChain security(HttpSecurity http,SecurityContextRepository contexts) throws Exception {
        return http.securityContext(c->c.securityContextRepository(contexts))
            .authorizeHttpRequests(a->a.requestMatchers("/","/index.html","/styles.css","/app.js","/favicon.svg","/api/auth/**","/actuator/health","/api-docs.json","/guide.html","/error").permitAll().anyRequest().authenticated())
            .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->{res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"detail\":\"Your session has expired. Please open a new workspace.\"}");})
                .accessDeniedHandler((req,res,ex)->{res.setStatus(403);res.setContentType("application/json");res.getWriter().write("{\"detail\":\"Security token expired. Refresh the page and try again.\"}");}))
            .headers(h->h.contentSecurityPolicy(c->c.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; connect-src 'self'; frame-ancestors 'none'; base-uri 'self'; form-action 'self'")))
            .logout(l->l.logoutUrl("/api/auth/logout").logoutSuccessHandler((req,res,auth)->res.setStatus(204)))
            .build();
    }
}
