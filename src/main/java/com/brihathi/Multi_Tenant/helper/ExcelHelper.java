
package com.brihathi.Multi_Tenant.helper;

import com.brihathi.Multi_Tenant.dto.UserExcelDTO;
import com.brihathi.Multi_Tenant.dto.EducatorExcelDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

public class ExcelHelper {

    // ================================
    // FILE FORMAT CHECK
    // ================================
    public static boolean isExcelFormat(MultipartFile file) {
        return Objects.equals(
                file.getContentType(),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }

    // =====================================================
    // USERS EXCEL PARSER (UNCHANGED – SAFE)
    // =====================================================
    public static List<UserExcelDTO> excelToUserList(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<UserExcelDTO> users = new ArrayList<>();
            int rowNumber = 0;

            while (rows.hasNext()) {
                Row row = rows.next();
                if (rowNumber++ == 0) continue;

                UserExcelDTO dto = new UserExcelDTO();
                dto.setName(getString(row.getCell(0)));
                dto.setEnrollmentId(getString(row.getCell(1)));
                dto.setPassword(getString(row.getCell(2)));
                dto.setPhoneNumber(getLong(row.getCell(3)));
                dto.setDateOfBirth(getDate(row.getCell(4)));
                dto.setGrade(getString(row.getCell(5)));
                dto.setState(getString(row.getCell(6)));
                dto.setCity(getString(row.getCell(7)));
                dto.setBranch(getString(row.getCell(8)));
                dto.setBatch(getString(row.getCell(9)));
                dto.setParentName(getString(row.getCell(10)));
                dto.setParentPhone(getLong(row.getCell(11)));
                dto.setTenantId(getLong(row.getCell(12)));

                users.add(dto);
            }

            workbook.close();
            return users;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user excel: " + e.getMessage());
        }
    }

    // =====================================================
    // EDUCATORS EXCEL PARSER (NEW – SAME STYLE)
    // =====================================================
    public static List<EducatorExcelDTO> excelToEducatorList(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<EducatorExcelDTO> educators = new ArrayList<>();
            int rowNumber = 0;

            while (rows.hasNext()) {
                Row row = rows.next();
                if (rowNumber++ == 0) continue;

                EducatorExcelDTO dto = new EducatorExcelDTO();
                dto.setEducatorName(getString(row.getCell(0)));
                dto.setEmail(getString(row.getCell(1)));
                dto.setPassword(getString(row.getCell(2)));
                dto.setPhoneNumber(getLong(row.getCell(3)));
                dto.setSubject(getString(row.getCell(4)));
                dto.setTenantId(getLong(row.getCell(5)));

                educators.add(dto);
            }

            workbook.close();
            return educators;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse educator excel: " + e.getMessage());
        }
    }

    // ================================
    // SAFE STRING READER
    // ================================
    private static String getString(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue().trim();
                } catch (Exception e) {
                    yield String.valueOf((long) cell.getNumericCellValue());
                }
            }
            default -> null;
        };
    }

    // ================================
    // SAFE LONG READER
    // ================================
    private static Long getLong(Cell cell) {
        if (cell == null) return null;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (long) cell.getNumericCellValue();
                case STRING:
                    String raw = cell.getStringCellValue()
                            .trim()
                            .replaceAll("[^0-9]", "");
                    return raw.isEmpty() ? null : Long.parseLong(raw);
                case FORMULA:
                    try {
                        return (long) cell.getNumericCellValue();
                    } catch (Exception e) {
                        String f = cell.getStringCellValue()
                                .trim()
                                .replaceAll("[^0-9]", "");
                        return Long.parseLong(f);
                    }
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // ================================
    // SAFE DATE READER
    // ================================
    private static LocalDate getDate(Cell cell) {
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }

            if (cell.getCellType() == CellType.STRING) {
                String text = cell.getStringCellValue().trim();
                if (text.isEmpty() || text.matches("\\d+")) return null;
                return LocalDate.parse(text);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
