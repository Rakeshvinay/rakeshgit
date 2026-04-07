package com.brihathi.Multi_Tenant.repository;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.dto.StudentSearchDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	// You can add custom queries here
    Optional<User> findByEnrollmentId(String enrollmentId);
    Optional<User> findByPhoneNumber(Long PhoneNumber);
    Optional<User> findByTenantId(Long tenantId);
    boolean existsByEnrollmentId(String enrollmentId);
    boolean existsByPhoneNumber(Long phoneNumber);
    boolean existsByTenantIdAndBranch(Long tenantId, String branch);
    boolean existsByTenantIdAndBranchAndBatch(Long tenantId, String branch, String batch);


    Optional<User> findByUserIdAndTenantId(Long userId, Long tenantId);

    Optional<User> findByEnrollmentIdAndTenant_TenantId(String enrollmentId, Long tenantId);
 
    Optional<User> findByPhoneNumberAndTenant_TenantId(Long phoneNumber, Long tenantId);
 
    // @Query("SELECT count(u.userId) FROM User u WHERE u.subscribed = 'YES'")
    // long countBySubscribedTrue();
    // Optional<User> findByReferralCode(String referralCode);

    // @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.referralCode = :referralCode")
    // boolean existsByReferralCode(@Param("referralCode") String referralCode);

    // @Query("SELECT u.wallet FROM User u WHERE u.userId = :userId")
    // Double findWalletAmountByUserId(@Param("userId") Long userId);

    // @Query("SELECT u.referralCode FROM User u WHERE u.userId = :userId")
    // String findReferralCodeByUserId(@Param("userId") Long userId);


    List<User> findByTenantIdAndBranchAndBatch(
        Long tenantId,
        String branch,
        String batch
);

Optional<User> findByNameIgnoreCase(String name);



// @Query("""
// SELECT new com.brihathi.Multi_Tenant.dto.StudentSearchDTO(
//     u.userId,
//     u.name,
//     u.enrollmentId
// )
// FROM User u
// WHERE u.tenantId = :tenantId
// AND (
//      :search IS NULL OR
//      LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
//      LOWER(u.enrollmentId) LIKE LOWER(CONCAT('%', :search, '%'))
// )
// ORDER BY u.name ASC
// """)
// List<StudentSearchDTO> searchStudents(Long tenantId, String search);

 
@Query("""
    SELECT new com.brihathi.Multi_Tenant.dto.StudentSearchDTO(
        u.userId,
        u.name,
        u.enrollmentId
    )
    FROM User u
    WHERE u.tenantId = :tenantId
    AND (
        :search IS NULL OR
        LOWER(u.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
        LOWER(u.enrollmentId) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
    )
    ORDER BY u.name ASC
    """)
    List<StudentSearchDTO> searchStudents(
            @Param("tenantId") Long tenantId,
            @Param("search") String search
    );
    

//     @Query("""
//     SELECT u FROM User u
//     WHERE u.updatedAt BETWEEN :from AND :to
// """)
// List<User> findRecentlyUpdatedUsers(
//         @Param("from") LocalDateTime from,
//         @Param("to") LocalDateTime to
// );
@Query(value = """
    SELECT * FROM users u
    WHERE u.updated_at BETWEEN :from AND :to
    AND ABS(EXTRACT(EPOCH FROM (u.updated_at - u.last_login))) > 30
""", nativeQuery = true)
List<User> findRecentlyUpdatedUsers(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
);
}
