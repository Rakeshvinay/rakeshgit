package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.Educator;
import com.brihathi.Multi_Tenant.entity.User;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
 
@Repository
public interface EducatorRepository extends JpaRepository<Educator, Long> {
 
    Optional<Educator> findByEmailAndTenantId(String email, Long tenantId);
    Optional<Educator> findByEmail(String email);
    Optional<Educator> findByPhoneNumberAndTenantId(Long phoneNumber, Long tenantId);
    boolean existsByPhoneNumber(Long phoneNumber);
    boolean existsByEmailAndTenantId(String email, Long tenantId);
    Optional<Educator> findByPhoneNumber(Long phoneNumber);

    // @Query("""
    //     SELECT e FROM Educator e
    //     WHERE e.updatedAt BETWEEN :from AND :to
    // """)
    // List<Educator> findRecentlyUpdatedEducators(
    //         @Param("from") LocalDateTime from,
    //         @Param("to") LocalDateTime to
    // );
    @Query(value ="""
    SELECT * FROM educators e
WHERE e.updated_at BETWEEN :from AND :to
AND ABS(EXTRACT(EPOCH FROM (e.updated_at - e.last_login))) > 30
""", nativeQuery = true)

List<Educator> findRecentlyUpdatedEducators(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
);



List<Educator> findByTenantId(Long tenantId);
}
 