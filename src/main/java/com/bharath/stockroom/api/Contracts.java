package com.bharath.stockroom.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import com.bharath.stockroom.domain.*;

public final class Contracts {
    private Contracts() {}
    public record Register(@NotBlank @Size(max=60) String name,@NotBlank @Email @Size(max=254) String email,@NotBlank @Size(min=10,max=64) String password) {}
    public record Login(@NotBlank @Email @Size(max=254) String email,@NotBlank @Size(max=64) String password) {}
    public record ProductInput(@NotBlank @Pattern(regexp="[A-Za-z0-9-]{2,32}") String sku,@NotBlank @Size(max=100) String name,@NotBlank @Size(max=40) String category,@NotNull @DecimalMin("0.01") @DecimalMax("9999999.99") @Digits(integer=7,fraction=2) BigDecimal price,@Min(0) @Max(1000000) int stock,@Min(0) @Max(100000) int reorderPoint) {}
    public record StockInput(@Min(-1000000) @Max(1000000) int change,@NotBlank @Size(max=120) String reason) {}
    public record LineInput(@NotNull UUID productId,@Min(1) @Max(10000) int quantity) {}
    public record OrderInput(@NotBlank @Size(max=100) String customer,@NotBlank @Email @Size(max=254) String email,@NotEmpty @Size(max=30) List<@Valid LineInput> lines) {}
    public record StatusInput(@NotBlank @Pattern(regexp="SHIPPED|CANCELLED") String status) {}
    public record AccountView(UUID id,String name,boolean demo,boolean ephemeral) {}
    public record ProductView(UUID id,String sku,String name,String category,BigDecimal price,int stock,int reorderPoint,long version) {
        public static ProductView of(Product p) {return new ProductView(p.id,p.sku,p.name,p.category,p.price,p.stock,p.reorderPoint,p.version);}
    }
    public record LineView(UUID productId,String name,String sku,int quantity,BigDecimal unitPrice) {}
    public record OrderView(UUID id,String reference,String customer,String email,String status,BigDecimal total,Instant createdAt,List<LineView> lines) {
        public static OrderView of(SalesOrder o) {return new OrderView(o.id,"SO-"+o.id.toString().substring(0,8).toUpperCase(Locale.ROOT),o.customer,o.email,o.status,o.total,o.createdAt,o.lines.stream().map(l->new LineView(l.productId,l.productName,l.sku,l.quantity,l.unitPrice)).toList());}
    }
    public record ActivityView(String kind,String message,Instant createdAt) {}
    public record DayView(String date,BigDecimal revenue,int orders) {}
    public record Overview(BigDecimal revenue,BigDecimal inventoryValue,int activeProducts,int lowStock,int orders,int pendingOrders,List<DayView> trend,List<ProductView> stockAlerts,List<OrderView> recentOrders,List<ActivityView> activity) {}
    public record PageView<T>(List<T> content,int page,int totalPages,long totalElements) {}
}
