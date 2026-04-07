package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.enums.NotificationStatus;
import com.brihathi.Multi_Tenant.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
 
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
 
import javax.management.Notification;
 
@Repository
public interface NotificationRepository
        extends JpaRepository<Notifications, Long> {
 
    List<Notifications>
    findByStatusAndScheduleTimeLessThanEqual(
            NotificationStatus status,
            LocalTime time
    );
 
    List<Notifications> findByUserIdAndTenantIdAndStatusInOrderByCreatedAtAsc(
        Long userId,
        Long tenantId,
        List<NotificationStatus> statuses
);
 
   
 
    boolean existsByUserIdAndTitle(Long userId, String title);
 
    List<Notifications> findByUserIdAndTenantIdAndStatusOrderByCreatedAtAsc(
            Long userId,
                Long tenantId,
            NotificationStatus status
    );
 
    // For students
    List<Notification> findByUserIdAndStatus(
            Long userId,
            NotificationStatus status
    );
 
    // For scheduler
    List<Notifications> findByStatus(
            NotificationStatus status
    );
 
    boolean existsByUserIdAndScheduledExamIdAndTitle(
        Long userId,
        Long scheduledExamId,
        String title
        );
 
        boolean existsByUserIdAndExamIdAndTitle(
        Long userId,
        Long examId,
        String title
        );
 


        List<Notifications> findByEducatorIdAndTenantIdAndStatusInOrderByCreatedAtAsc(
                Long educatorId,
                Long tenantId,
                List<NotificationStatus> statuses
        );
        

        List<Notifications> findByEducatorIdAndUserIdIsNull(
                Long educatorId
        );

        boolean existsByEducatorIdAndTitleAndCreatedAtAfter(
                Long educatorId,
                String title,
                LocalDateTime time
        );

        boolean existsByTenantIdAndTitleAndCreatedAtAfter(
                Long tenantId,
                String title,
                LocalDateTime time
        );
        void deleteById(Long userId);
        void deleteByUserId(Long userId);
        void deleteByEducatorId(Long educatorId);

}
 
 
 