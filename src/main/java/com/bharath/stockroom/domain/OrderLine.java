package com.bharath.stockroom.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
public class OrderLine {
    public UUID productId;
    @Column(length=100) public String productName;
    @Column(length=32) public String sku;
    public int quantity;
    @Column(precision=12,scale=2) public BigDecimal unitPrice;
    protected OrderLine() {}
    public OrderLine(Product p,int quantity) {this.productId=p.id;this.productName=p.name;this.sku=p.sku;this.quantity=quantity;this.unitPrice=p.price;}
}
