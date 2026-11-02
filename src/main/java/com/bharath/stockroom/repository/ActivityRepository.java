package com.bharath.stockroom.repository;
import com.bharath.stockroom.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ActivityRepository extends JpaRepository<Activity,UUID> {
    List<Activity> findTop12ByOwnerIdOrderByCreatedAtDesc(UUID ownerId);
    void deleteByOwnerId(UUID ownerId);
}
