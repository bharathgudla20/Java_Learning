package com.bharath.stockroom.repository;
import com.bharath.stockroom.domain.Account;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;
import java.util.*;
import java.time.Instant;
public interface AccountRepository extends JpaRepository<Account,UUID> {
    Optional<Account> findByEmail(String email);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select a from Account a where a.id=:id")
    Optional<Account> lockById(@Param("id") UUID id);
    List<Account> findByDemoTrueAndCreatedAtBefore(Instant cutoff);
}
