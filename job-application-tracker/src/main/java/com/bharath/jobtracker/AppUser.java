package com.bharath.jobtracker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 254)
    private String email;
    @JsonIgnore
    @Column(nullable = false, length = 100)
    private String passwordHash;

    protected AppUser() {}
    public AppUser(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    @JsonIgnore public String getPasswordHash() { return passwordHash; }
}
