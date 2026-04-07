
package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.entity.QuestionPublic;
import com.brihathi.Multi_Tenant.dto.ExamInfoDTO;
import com.brihathi.Multi_Tenant.enums.Subject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionUploadService {

    void validateFile(MultipartFile file);

    void importExcelFile(MultipartFile file, String requestSubdomain) throws Exception;

    // QuestionPublic saveQuestion(QuestionPublic question, String requestSubdomain) throws Exception;

    List<QuestionPublic> getQuestionsBySubject(Subject subject);

    // ExamInfoDTO getExamInfo(String requestSubdomain);
}
