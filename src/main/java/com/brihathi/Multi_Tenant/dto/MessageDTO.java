

package com.brihathi.Multi_Tenant.dto;
 
import java.io.Serializable;
 
public class MessageDTO implements Serializable {
 
    private String type;   // e.g. EXAM_RESULT, SCORE_PREDICTOR, etc.
    private Long userId;
    private Long examId;
 
    public MessageDTO() {
    }
 
    public MessageDTO(String type, Long userId, Long examId) {
        this.type = type;
        this.userId = userId;
        this.examId = examId;
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
 
    public Long getExamId() {
        return examId;
    }
 
    public void setExamId(Long examId) {
        this.examId = examId;
    }
 
    @Override
    public String toString() {
        return "MessageDTO{" +
                "type='" + type + '\'' +
                ", userId=" + userId +
                ", examId=" + examId +
                '}';
    }
}
