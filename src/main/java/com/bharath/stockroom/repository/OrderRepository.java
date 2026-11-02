package com.bharath.stockroom.repository;
import com.bharath.stockroom.domain.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface OrderRepository extends JpaRepository<SalesOrder,UUID> {
    Optional<SalesOrder> findByOwnerIdAndRequestKey(UUID ownerId,String requestKey);
    Optional<SalesOrder> findByIdAndOwnerId(UUID id,UUID ownerId);
    List<SalesOrder> findByOwnerIdOrderByCreatedAtDesc(UUID ownerId);
    long countByOwnerId(UUID ownerId);
    void deleteByOwnerId(UUID ownerId);
}
