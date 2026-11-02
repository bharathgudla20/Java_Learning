package com.bharath.stockroom.repository;
import com.bharath.stockroom.domain.Product;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.util.*;
public interface ProductRepository extends JpaRepository<Product,UUID> {
    Optional<Product> findByIdAndOwnerId(UUID id,UUID ownerId);
    List<Product> findByOwnerIdAndArchivedFalseOrderByName(UUID ownerId);
    long countByOwnerId(UUID ownerId);
    boolean existsByOwnerIdAndSku(UUID ownerId,String sku);
    @Query("select p from Product p where p.ownerId=:owner and p.archived=false and (lower(p.name) like :search escape '!' or lower(p.sku) like :search escape '!') and (:category='' or p.category=:category) and (:low=false or p.stock<=p.reorderPoint)")
    Page<Product> search(@Param("owner") UUID owner,@Param("search") String search,@Param("category") String category,@Param("low") boolean low,Pageable pageable);
    void deleteByOwnerId(UUID ownerId);
}
