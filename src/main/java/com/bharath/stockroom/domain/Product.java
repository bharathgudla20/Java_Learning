package com.bharath.stockroom.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="products", uniqueConstraints=@UniqueConstraint(columnNames={"owner_id","sku"}), indexes=@Index(columnList="owner_id"))
public class Product {
    @Id public UUID id=UUID.randomUUID();
    @Column(name="owner_id", nullable=false) public UUID ownerId;
    @Column(nullable=false,length=32) public String sku;
    @Column(nullable=false,length=100) public String name;
    @Column(nullable=false,length=40) public String category;
    @Column(nullable=false,precision=12,scale=2) public BigDecimal price;
    public int stock;
    public int reorderPoint;
    public boolean archived;
    @Version public long version;
    public Instant createdAt=Instant.now();
    protected Product() {}
    public Product(UUID ownerId, String sku, String name, String category, BigDecimal price, int stock, int reorderPoint) {
        this.ownerId=ownerId;this.sku=sku;this.name=name;this.category=category;this.price=price;this.stock=stock;this.reorderPoint=reorderPoint;
    }
}
