package com.bharath.jobtracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

// Spring implements this interface at runtime. Long is the ID type (generics).
public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {
    // JPQL uses Java entity/field names. Hibernate translates it to SQL.
    // LOCATE treats user input literally, including % and _.
    @Query("""
        select a from JobApplication a
        where a.owner.id = :ownerId and (:status is null or a.status = :status)
          and (locate(lower(:search), lower(a.company)) > 0
            or locate(lower(:search), lower(a.role)) > 0)
        order by a.appliedOn desc, a.id desc
        """)
    List<JobApplication> search(@Param("search") String search, @Param("status") Status status,
                                @Param("ownerId") Long ownerId);
    Optional<JobApplication> findByIdAndOwnerId(Long id, Long ownerId);
    List<JobApplication> findByOwnerId(Long ownerId);
}
