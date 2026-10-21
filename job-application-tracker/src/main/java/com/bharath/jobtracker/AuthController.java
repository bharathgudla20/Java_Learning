package com.bharath.jobtracker;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AccountService accounts;
    public AuthController(AccountService accounts) { this.accounts = accounts; }

    public record Registration(
        @NotBlank @Email @Size(max = 254) String email,
        @NotNull @Size(min = 12, max = 72) String password) {}

    // The browser fetches a session-bound CSRF token before POST/PUT/DELETE requests.
    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("token", token.getToken(), "headerName", token.getHeaderName());
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody Registration request) {
        accounts.register(request.email(), request.password());
        return ResponseEntity.status(201).body(Map.of("message", "Account created. You can now sign in."));
    }

    @GetMapping("/me")
    public Map<String, String> me() { return Map.of("email", accounts.currentUser().getEmail()); }
}
