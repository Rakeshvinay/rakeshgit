

package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.entity.QuestionPublic;
import com.brihathi.Multi_Tenant.entity.QuestionTenant;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.entity.Chapter;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.enums.QuestionStatus;
import com.brihathi.Multi_Tenant.repository.QuestionPublicRepository;
import com.brihathi.Multi_Tenant.repository.QuestionTenantRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.repository.TenantStaffRepository;
import com.brihathi.Multi_Tenant.repository.ChapterRepository;
import com.brihathi.Multi_Tenant.service.QuestionUploadService;
import com.brihathi.Multi_Tenant.dto.ExamInfoDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionUploadServiceImpl implements QuestionUploadService {

    private final QuestionPublicRepository questionPublicRepository;
    private final QuestionTenantRepository questionTenantRepository;
    private final TenantRepository tenantRepository;
    private final ChapterRepository chapterRepository;

    // ---------------- VALIDATION ----------------

    @Override
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("File is empty");
        if (!file.getOriginalFilename().endsWith(".xlsx"))
            throw new IllegalArgumentException("Only .xlsx files supported");
    }

    // ---------------- UPLOAD ----------------

    @Override
    @Transactional
    public void importExcelFile(MultipartFile file, String requestSubdomain) throws Exception {

        validateFile(file);

        Tenant tenant = tenantRepository.findBySubdomain(requestSubdomain)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found for subdomain: " + requestSubdomain));

        boolean usePublicTable =
                "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null)
                throw new IllegalArgumentException("Excel must have header row");

            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                idx.put(headerRow.getCell(i).getStringCellValue().trim().toLowerCase(), i);
            }

            validateRequiredColumns(idx);

            Map<Subject, AtomicInteger> counters =
                    initializeSubjectCounters(
                            usePublicTable
                                    ? questionPublicRepository.findAll()
                                    : Collections.emptyList()
                    );

            List<QuestionPublic> publicQuestions = new ArrayList<>();
            List<QuestionTenant> tenantQuestions = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

              
                Row row = sheet.getRow(i);

                Long excelTenantId = Long.parseLong(
                    getString(row, idx.get("tenant id"))
            );
            if (!excelTenantId.equals(tenant.getTenantId())) {
                throw new IllegalArgumentException(
                        "Tenant mismatch at row " + (i + 1)
                        + ". Excel tenant_id=" + excelTenantId
                        + ", URL tenant_id=" + tenant.getTenantId()
                );
            }
            
            
                if (row == null) continue;

                Subject subject = Subject.valueOf(getString(row, idx.get("subject")).toUpperCase());

                if (usePublicTable) {
                    QuestionPublic q = buildPublicQuestion(row, idx, subject, counters, tenant);
                    publicQuestions.add(q);
                } else {
                    QuestionTenant q = buildTenantQuestion(row, idx, subject, counters, tenant);
                    tenantQuestions.add(q);
                }
            }

            if (usePublicTable) {
                questionPublicRepository.saveAll(publicQuestions);
            } else {
                questionTenantRepository.saveAll(tenantQuestions);
            }
        }
    }

    // ---------------- BUILDERS ----------------

    private QuestionPublic buildPublicQuestion(Row row, Map<String, Integer> idx,
                                               Subject subject,
                                               Map<Subject, AtomicInteger> counters,
                                               Tenant tenant) {

        QuestionPublic q = new QuestionPublic();
        fillCommonFields(q, row, idx, subject, counters, tenant);
        return q;
    }

    private QuestionTenant buildTenantQuestion(Row row, Map<String, Integer> idx,
                                               Subject subject,
                                               Map<Subject, AtomicInteger> counters,
                                               Tenant tenant) {

        QuestionTenant q = new QuestionTenant();
        fillCommonFields(q, row, idx, subject, counters, tenant);
        return q;
    }

    private void fillCommonFields(Object obj, Row row, Map<String, Integer> idx,
                                  Subject subject,
                                  Map<Subject, AtomicInteger> counters,
                                  Tenant tenant) {

        String qid = generateQID(subject, counters);

        Chapter chapter = chapterRepository
                .findBySubjectAndChapter(subject.name(), getString(row, idx.get("chapter")))
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        Boolean isPyq = Boolean.parseBoolean(getString(row, idx.get("is pyq")));

        if (obj instanceof QuestionPublic q) {
            q.setQid(qid);
            q.setTenantId(tenant.getTenantId());
            q.setSubject(subject);
            q.setChapter(chapter.getChapter());
            q.setChapterId(chapter.getChapterId());
            q.setQuestionText(getString(row, idx.get("question text")));
            q.setAnswerOption1(getString(row, idx.get("answer option1")));
            q.setAnswerOption2(getString(row, idx.get("answer option2")));
            q.setAnswerOption3(getString(row, idx.get("answer option3")));
            q.setAnswerOption4(getString(row, idx.get("answer option4")));
            q.setCorrectAnswerOption(getString(row, idx.get("correct answer option")));
            q.setTopic(getString(row, idx.get("topic")));
            q.setSubTopic(getString(row, idx.get("sub topic")));
            q.setGrade(getString(row, idx.get("grade")));
            q.setDifficulty(Difficulty.valueOf(getString(row, idx.get("difficulty")).toUpperCase()));
            q.setStatus(QuestionStatus.valueOf(getString(row, idx.get("status")).toUpperCase()));
            q.setBriefExplanation(getString(row, idx.get("brief explanation")));
            q.setSource(getString(row, idx.get("source")));
            q.setIsPyq(isPyq);
            q.setTimesRepeated(0);
        }

        if (obj instanceof QuestionTenant q) {
            q.setQid(qid);
            q.setTenantId(tenant.getTenantId());
            q.setSubject(subject);
            q.setChapter(chapter.getChapter());
            q.setChapterId(chapter.getChapterId());
            q.setQuestionText(getString(row, idx.get("question text")));
            q.setAnswerOption1(getString(row, idx.get("answer option1")));
            q.setAnswerOption2(getString(row, idx.get("answer option2")));
            q.setAnswerOption3(getString(row, idx.get("answer option3")));
            q.setAnswerOption4(getString(row, idx.get("answer option4")));
            q.setCorrectAnswerOption(getString(row, idx.get("correct answer option")));
            q.setTopic(getString(row, idx.get("topic")));
            q.setSubTopic(getString(row, idx.get("sub topic")));
            q.setGrade(getString(row, idx.get("grade")));
            q.setDifficulty(Difficulty.valueOf(getString(row, idx.get("difficulty")).toUpperCase()));
            q.setStatus(QuestionStatus.valueOf(getString(row, idx.get("status")).toUpperCase()));
            q.setBriefExplanation(getString(row, idx.get("brief explanation")));
            q.setSource(getString(row, idx.get("source")));
            q.setIsPyq(isPyq);
            q.setTimesRepeated(0);
        }
    }

    // ---------------- HELPERS ----------------

    private void validateRequiredColumns(Map<String, Integer> map) {
        List<String> required = List.of(
                 "tenant id","subject", "chapter", "question text",
                "answer option1", "answer option2",
                "answer option3", "answer option4",
                "correct answer option",
                "topic", "sub topic",
                "grade", "difficulty", "status", "is pyq"
        );
        for (String col : required)
            if (!map.containsKey(col))
                throw new IllegalArgumentException("Missing column: " + col);
    }

    private String getString(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) return "";
    
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                yield String.valueOf((long) cell.getNumericCellValue());
            }
            case FORMULA -> cell.getCellFormula();
            case BLANK, _NONE, ERROR -> "";
        };
    }
    

    private Map<Subject, AtomicInteger> initializeSubjectCounters(List<QuestionPublic> list) {
        Map<Subject, AtomicInteger> map = new EnumMap<>(Subject.class);
        for (Subject s : Subject.values()) map.put(s, new AtomicInteger(0));

        for (QuestionPublic q : list) {
            if (q.getQid() != null) {
                Subject s = Subject.fromCode(q.getQid().substring(0, 3));
                int num = Integer.parseInt(q.getQid().substring(3));
                map.get(s).set(Math.max(map.get(s).get(), num));
            }
        }
        return map;
    }

    private String generateQID(Subject subject, Map<Subject, AtomicInteger> counters) {
        return String.format("%s%05d",
                subject.getCode(),
                counters.get(subject).incrementAndGet());
    }

    // ---------------- READ (unchanged) ----------------

    @Override
    public List<QuestionPublic> getQuestionsBySubject(Subject subject) {
        return questionPublicRepository.findBySubject(subject);
    }

    // @Override
    // public ExamInfoDTO getExamInfo() {
    //     return new ExamInfoDTO();
    // }
}
