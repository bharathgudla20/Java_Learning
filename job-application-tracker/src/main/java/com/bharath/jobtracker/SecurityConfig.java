package com.bharath.jobtracker;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }

    @Bean UserDetailsService userDetailsService(UserRepository users) {
        return email -> users.findByEmail(AccountService.normalizeEmail(email))
            .map(user -> User.withUsername(user.getEmail()).password(user.getPasswordHash()).roles("USER").build())
            .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));
    }

    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF stays enabled, including login, registration, and logout.
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/index.html", "/styles.css", "/app.js", "/auth.js", "/login.js", "/login.html",
                "/favicon.ico", "/error", "/api/auth/csrf", "/api/auth/register", "/actuator/health").permitAll()
            .anyRequest().authenticated())
            .requestCache(cache -> cache.disable())
            .exceptionHandling(errors -> errors.authenticationEntryPoint((request, response, exception) ->
                json(response, 401, "Please sign in to continue.")))
            .formLogin(login -> login.loginPage("/login.html").loginProcessingUrl("/api/auth/login")
                .usernameParameter("email")
                .successHandler((request, response, authentication) -> json(response, 200, "Signed in."))
                .failureHandler((request, response, exception) -> json(response, 401, "Invalid email or password.")))
            .logout(logout -> logout.logoutUrl("/api/auth/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) -> json(response, 200, "Signed out.")))
            .headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives(
                "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; " +
                "connect-src 'self'; object-src 'none'; base-uri 'self'; form-action 'self'; frame-ancestors 'none'")))
            .addFilterBefore(new AuthRateLimitFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    static void json(HttpServletResponse response, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
