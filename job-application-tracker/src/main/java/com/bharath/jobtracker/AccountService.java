package com.bharath.jobtracker;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class AccountService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    public AccountService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }
    public static String normalizeEmail(String email) { return email.trim().toLowerCase(Locale.ROOT); }

    public AppUser currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return users.findByEmail(email).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in again."));
    }

    @Transactional
    public void register(String email, String password) {
        String normalized = normalizeEmail(email);
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must use at most 72 UTF-8 bytes.");
        }
        if (users.existsByEmail(normalized)) throw registrationConflict();
        try {
            users.saveAndFlush(new AppUser(normalized, encoder.encode(password)));
        } catch (DataIntegrityViolationException ex) { throw registrationConflict(); }
    }

    private ResponseStatusException registrationConflict() {
        return new ResponseStatusException(HttpStatus.CONFLICT,
            "Unable to register this email. Try signing in or use another email.");
    }
}
