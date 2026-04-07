package com.brihathi.Multi_Tenant.dto;
import java.io.Serializable;

public class ExamResultMessageDTO implements Serializable {
    private Long examId;
    private Long userId;
    private String message;

    public ExamResultMessageDTO() {}

    public ExamResultMessageDTO(Long examId, Long userId) {
        this.examId = examId;
        this.userId = userId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ExamResultMessageDTO{" +
                "examId=" + examId +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                '}';
    }
} 