

package com.brihathi.Multi_Tenant.dto;
 
import java.io.Serializable;
 
public class ScheduledMessageDTO implements Serializable {
 
    private String type;   // e.g. EXAM_RESULT, SCORE_PREDICTOR, etc.
    private Long userId;
    private Long eduScheduledExamId;
    private Long tenantId;

 
    public ScheduledMessageDTO() {
    }
 
    public ScheduledMessageDTO(String type, Long userId, Long eduScheduledExamId, Long tenantId) {
        this.type = type;
        this.userId = userId;
        this.eduScheduledExamId = eduScheduledExamId;
        this.tenantId = tenantId;
    }
 
    public String getType() {
        return type;
    }
 
    public void setType(String type) {
        this.type = type;
    }
 
    public Long getUserId() {
        return userId;
    }
 
    public void setUserId(Long userId) {
        this.userId = userId;
    }
 
    public Long getEduScheduledExamId() {
        return eduScheduledExamId;
    }
 
    public void setEduScheduledExamId(Long eduScheduledExamId) {
        this.eduScheduledExamId = eduScheduledExamId;
    }
 
    public Long getTenantId() {
        return tenantId;
    }
 
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
 
    @Override
    public String toString() {
        return "MessageDTO{" +
                "type='" + type + '\'' +
                ", userId=" + userId +
                ", eduScheduledExamId=" + eduScheduledExamId +
                ", tenantId=" + tenantId +
                '}';
    }
}
