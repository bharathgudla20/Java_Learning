package com.bharath.stockroom.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="accounts")
public class Account {
    @Id public UUID id = UUID.randomUUID();
    @Column(unique=true, length=254) public String email;
    @Column(nullable=false) public String passwordHash;
    @Column(nullable=false, length=60) public String name;
    public boolean demo;
    public Instant createdAt = Instant.now();
    protected Account() {}
    public Account(String email, String passwordHash, String name, boolean demo) {
        this.email=email; this.passwordHash=passwordHash; this.name=name; this.demo=demo;
    }
}
