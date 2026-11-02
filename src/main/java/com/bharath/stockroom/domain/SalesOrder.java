package com.bharath.stockroom.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity @Table(name="sales_orders", uniqueConstraints=@UniqueConstraint(columnNames={"owner_id","request_key"}), indexes=@Index(columnList="owner_id"))
public class SalesOrder {
    @Id public UUID id=UUID.randomUUID();
    @Column(name="owner_id",nullable=false) public UUID ownerId;
    @Column(name="request_key",nullable=false,length=80) public String requestKey;
    @Column(nullable=false,length=100) public String customer;
    @Column(nullable=false,length=254) public String email;
    @Column(nullable=false,length=12) public String status="PLACED";
    @Column(nullable=false,precision=14,scale=2) public BigDecimal total=BigDecimal.ZERO;
    public Instant createdAt=Instant.now();
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="order_lines",joinColumns=@JoinColumn(name="order_id"))
    @OrderColumn(name="line_position")
    public List<OrderLine> lines=new ArrayList<>();
    protected SalesOrder() {}
    public SalesOrder(UUID ownerId,String key,String customer,String email) {this.ownerId=ownerId;this.requestKey=key;this.customer=customer;this.email=email;}
}
