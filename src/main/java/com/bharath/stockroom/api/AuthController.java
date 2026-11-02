package com.bharath.stockroom.api;

import com.bharath.stockroom.api.Contracts.*;
import com.bharath.stockroom.domain.Account;
import com.bharath.stockroom.repository.AccountRepository;
import com.bharath.stockroom.service.AccountService;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AccountService service;private final AccountRepository accounts;private final SecurityContextRepository contexts;
    @Value("${stockroom.ephemeral}") private boolean ephemeral;
    public AuthController(AccountService s,AccountRepository a,SecurityContextRepository c) {service=s;accounts=a;contexts=c;}
    @GetMapping("/csrf") public Map<String,String> csrf(CsrfToken token) {return Map.of("token",token.getToken(),"headerName",token.getHeaderName());}
    @GetMapping("/me") public AccountView me(Authentication auth) {
        if(auth==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Please sign in or open a demo.");
        Account a=accounts.findById(UUID.fromString(auth.getName())).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Workspace expired."));return view(a);
    }
    @PostMapping("/demo") public AccountView demo(HttpServletRequest req,HttpServletResponse res) {return signIn(service.demo(),req,res);}
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public AccountView register(@Valid @RequestBody Register input,HttpServletRequest req,HttpServletResponse res) {return signIn(service.register(input),req,res);}
    @PostMapping("/login") public AccountView login(@Valid @RequestBody Login input,HttpServletRequest req,HttpServletResponse res) {return signIn(service.login(input),req,res);}
    private AccountView signIn(Account a,HttpServletRequest req,HttpServletResponse res) {
        req.getSession();req.changeSessionId();
        var auth=UsernamePasswordAuthenticationToken.authenticated(a.id.toString(),null,List.of(new SimpleGrantedAuthority(a.demo?"ROLE_DEMO":"ROLE_OWNER")));
        var context=SecurityContextHolder.createEmptyContext();context.setAuthentication(auth);SecurityContextHolder.setContext(context);contexts.saveContext(context,req,res);
        return view(a);
    }
    private AccountView view(Account a) {return new AccountView(a.id,a.name,a.demo,ephemeral);}
}
