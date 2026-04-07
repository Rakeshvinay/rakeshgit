
package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.entity.Chapter;
import com.brihathi.Multi_Tenant.repository.ChapterRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.service.ChapterService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ChapterServiceImpl implements ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private TenantRepository tenantRepository;

    private static final List<String> VALID_SUBJECTS =
            Arrays.asList("PHYSICS", "CHEMISTRY", "BOTANY", "ZOOLOGY");

    @Override
    public List<Chapter> uploadChaptersFromExcel(
            MultipartFile file,
            String requestSubdomain) throws IOException {

        // 🔐 TENANT SUBDOMAIN VALIDATION
        if (!tenantRepository.existsBySubdomain(requestSubdomain)) {
            throw new IllegalArgumentException(
                    "Invalid tenant subdomain: " + requestSubdomain
            );
        }

        List<Chapter> chapters = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String subject = getCellValueAsString(row.getCell(2));
            if (subject == null) continue;

            subject = subject.toUpperCase();
            if (!VALID_SUBJECTS.contains(subject)) {
                workbook.close();
                throw new IllegalArgumentException(
                        "Invalid subject: " + subject
                );
            }

            Integer numberOfQuestions = getCellValueAsInteger(row.getCell(4));
            if (numberOfQuestions == null) {
                workbook.close();
                throw new IllegalArgumentException(
                        "number_of_questions cannot be null at row " + (i + 1)
                );
            }

            Chapter chapter = new Chapter();
            chapter.setChapterId(getCellValueAsString(row.getCell(0)));
            chapter.setChapter(getCellValueAsString(row.getCell(1)));
            chapter.setSubject(subject);
            chapter.setWeightage(getCellValueAsDouble(row.getCell(3)));
            chapter.setNumberOfQuestions(numberOfQuestions);

            chapters.add(chapter);
        }

        workbook.close();
        return chapterRepository.saveAll(chapters);
    }

    @Override
    public Chapter createAllChapter() {
        chapterRepository.findById("ALL-ALL")
                .ifPresent(c -> {
                    throw new IllegalArgumentException("ALL chapter already exists");
                });

        Chapter all = new Chapter();
        all.setChapterId("ALL-ALL");
        all.setChapter("ALL");
        all.setSubject("ALL");
        all.setWeightage(100.0);
        all.setNumberOfQuestions(0);
        return chapterRepository.save(all);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return cell.getCellType() == CellType.NUMERIC
                ? String.valueOf((int) cell.getNumericCellValue())
                : cell.getStringCellValue().trim();
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return null;
        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;
        try {
            return (int) cell.getNumericCellValue();
        } catch (Exception e) {
            return null;
        }
    }
}
