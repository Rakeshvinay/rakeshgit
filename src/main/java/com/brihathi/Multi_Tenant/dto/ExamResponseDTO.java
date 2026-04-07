package com.brihathi.Multi_Tenant.dto;
import com.brihathi.Multi_Tenant.entity.Exam;
import com.brihathi.Multi_Tenant.entity.QuestionPublic;
import com.brihathi.Multi_Tenant.entity.QuestionTenant;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import lombok.Data;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ExamResponseDTO {
    private Long examId;
    private Long userId;
    private String subjectId;
    private String chapterId;
    private String examType;
    private Difficulty difficulty;
    private String grade;
    private Integer totalMarks;
    private Duration totalDuration;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Exam.ExamStatus status;
    private List<SimpleQuestionDTO> questions;

    public static ExamResponseDTO fromExam(Exam exam, List<QuestionPublic> questions) {
        ExamResponseDTO dto = new ExamResponseDTO();
        dto.setExamId(exam.getExamId());
        dto.setUserId(exam.getUser().getUserId());
        dto.setSubjectId(exam.getSubjectId());
        dto.setChapterId(exam.getChapterId());
        dto.setExamType(exam.getExamType().toString());
        dto.setDifficulty(exam.getDifficulty());
        dto.setGrade(exam.getGrade());
        dto.setTotalMarks(exam.getTotalMarks());
        dto.setTotalDuration(exam.getTotalDuration());
        dto.setStartDate(exam.getStartDate());
        dto.setEndDate(exam.getEndDate());
        dto.setStatus(exam.getStatus());
        dto.setQuestions(questions.stream()
            .map(SimpleQuestionDTO::fromQuestion)
            .collect(Collectors.toList()));
        return dto;
    }
} 