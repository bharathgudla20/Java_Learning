package com.bharath.stockroom.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="activities",indexes=@Index(columnList="owner_id"))
public class Activity {
    @Id public UUID id=UUID.randomUUID();
    @Column(name="owner_id",nullable=false) public UUID ownerId;
    @Column(nullable=false,length=30) public String kind;
    @Column(nullable=false,length=240) public String message;
    public Instant createdAt=Instant.now();
    protected Activity() {}
    public Activity(UUID ownerId,String kind,String message) {this.ownerId=ownerId;this.kind=kind;this.message=message;}
}
