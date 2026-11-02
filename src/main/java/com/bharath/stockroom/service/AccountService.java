package com.bharath.stockroom.service;

import com.bharath.stockroom.domain.Account;
import com.bharath.stockroom.repository.AccountRepository;
import com.bharath.stockroom.api.Contracts.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service
public class AccountService {
    private final AccountRepository accounts;private final PasswordEncoder encoder;private final StockroomService stockroom;
    public AccountService(AccountRepository a,PasswordEncoder e,StockroomService s) {accounts=a;encoder=e;stockroom=s;}
    @Transactional public Account demo() {
        Account a=accounts.save(new Account(null,encoder.encode(UUID.randomUUID().toString()),"Demo workspace",true));stockroom.seed(a.id);return a;
    }
    @Transactional public Account register(Register input) {
        if(input.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password must fit within 72 UTF-8 bytes.");
        String email=input.email().trim().toLowerCase(Locale.ROOT);
        if(accounts.findByEmail(email).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists.");
        Account a=accounts.save(new Account(email,encoder.encode(input.password()),input.name().trim(),false));stockroom.seed(a.id);return a;
    }
    public Account login(Login input) {
        if(input.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect.");
        Account a=accounts.findByEmail(input.email().trim().toLowerCase(Locale.ROOT)).orElse(null);
        if(!encoder.matches(input.password(),a==null?"$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy":a.passwordHash) || a==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect.");
        return a;
    }
}
