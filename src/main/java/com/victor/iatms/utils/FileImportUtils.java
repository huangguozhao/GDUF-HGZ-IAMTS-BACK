package com.victor.iatms.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.iatms.entity.dto.ImportTestCaseDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件导入工具类
 */
@Component
public class FileImportUtils {

    /**
     * 解析上传的文件
     * @param file 上传的文件
     * @return 解析后的测试用例列表
     * @throws IOException 文件解析异常
     */
    public List<ImportTestCaseDTO> parseFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String extension = getFileExtension(fileName).toLowerCase();
        
        switch (extension) {
            case "xlsx":
            case "xls":
                return parseExcelFile(file);
            case "csv":
                return parseCsvFile(file);
            default:
                throw new IllegalArgumentException("不支持的文件格式: " + extension);
        }
    }

    /**
     * 解析Excel文件
     */
    private List<ImportTestCaseDTO> parseExcelFile(MultipartFile file) throws IOException {
        List<ImportTestCaseDTO> testCases = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook;
            String fileName = file.getOriginalFilename();
            
            if (fileName != null && fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new HSSFWorkbook(inputStream);
            }
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 获取标题行
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel文件格式错误：缺少标题行");
            }
            
            // 创建列索引映射
            ColumnMapping columnMapping = createColumnMapping(headerRow);
            
            // 解析数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                ImportTestCaseDTO testCase = parseExcelRow(row, columnMapping, i + 1);
                if (testCase != null) {
                    testCases.add(testCase);
                }
            }
            
            workbook.close();
        }
        
        return testCases;
    }

    /**
     * 解析CSV文件
     */
    private List<ImportTestCaseDTO> parseCsvFile(MultipartFile file) throws IOException {
        List<ImportTestCaseDTO> testCases = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream()) {
            String content = new String(inputStream.readAllBytes(), "UTF-8");
            String[] lines = content.split("\n");
            
            if (lines.length < 2) {
                throw new IllegalArgumentException("CSV文件格式错误：至少需要标题行和一行数据");
            }
            
            // 解析标题行
            String[] headers = parseCsvLine(lines[0]);
            ColumnMapping columnMapping = createColumnMappingFromHeaders(headers);
            
            // 解析数据行
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;
                
                ImportTestCaseDTO testCase = parseCsvLine(line, columnMapping, i + 1);
                if (testCase != null) {
                    testCases.add(testCase);
                }
            }
        }
        
        return testCases;
    }

    /**
     * 创建Excel列映射
     */
    private ColumnMapping createColumnMapping(Row headerRow) {
        ColumnMapping mapping = new ColumnMapping();
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null) continue;
            
            String headerName = getCellValueAsString(cell).toLowerCase().trim();
            mapping.setColumnIndex(headerName, i);
        }
        
        return mapping;
    }

    /**
     * 创建CSV列映射
     */
    private ColumnMapping createColumnMappingFromHeaders(String[] headers) {
        ColumnMapping mapping = new ColumnMapping();
        
        for (int i = 0; i < headers.length; i++) {
            String headerName = headers[i].toLowerCase().trim();
            mapping.setColumnIndex(headerName, i);
        }
        
        return mapping;
    }

    /**
     * 解析Excel行
     */
    private ImportTestCaseDTO parseExcelRow(Row row, ColumnMapping mapping, int rowNumber) {
        ImportTestCaseDTO testCase = new ImportTestCaseDTO();
        testCase.setRowNumber(rowNumber);
        
        // 解析各个字段
        testCase.setCaseCode(getCellValueAsString(row.getCell(mapping.getColumnIndex("case_code"))));
        testCase.setName(getCellValueAsString(row.getCell(mapping.getColumnIndex("name"))));
        testCase.setDescription(getCellValueAsString(row.getCell(mapping.getColumnIndex("description"))));
        testCase.setPriority(getCellValueAsString(row.getCell(mapping.getColumnIndex("priority"))));
        testCase.setSeverity(getCellValueAsString(row.getCell(mapping.getColumnIndex("severity"))));
        
        // 解析标签
        String tagsStr = getCellValueAsString(row.getCell(mapping.getColumnIndex("tags")));
        if (tagsStr != null && !tagsStr.trim().isEmpty()) {
            testCase.setTags(parseTags(tagsStr));
        }
        
        testCase.setPreConditions(getCellValueAsString(row.getCell(mapping.getColumnIndex("pre_conditions"))));
        testCase.setTestSteps(getCellValueAsString(row.getCell(mapping.getColumnIndex("test_steps"))));
        testCase.setRequestOverride(getCellValueAsString(row.getCell(mapping.getColumnIndex("request_override"))));
        
        // 解析数字字段
        Cell expectedStatusCell = row.getCell(mapping.getColumnIndex("expected_http_status"));
        if (expectedStatusCell != null && expectedStatusCell.getCellType() == CellType.NUMERIC) {
            testCase.setExpectedHttpStatus((int) expectedStatusCell.getNumericCellValue());
        }
        
        testCase.setExpectedResponseBody(getCellValueAsString(row.getCell(mapping.getColumnIndex("expected_response_body"))));
        testCase.setAssertions(getCellValueAsString(row.getCell(mapping.getColumnIndex("assertions"))));
        testCase.setExtractors(getCellValueAsString(row.getCell(mapping.getColumnIndex("extractors"))));
        testCase.setValidators(getCellValueAsString(row.getCell(mapping.getColumnIndex("validators"))));
        
        // 解析布尔字段
        String isEnabledStr = getCellValueAsString(row.getCell(mapping.getColumnIndex("is_enabled")));
        if (isEnabledStr != null) {
            testCase.setIsEnabled("true".equalsIgnoreCase(isEnabledStr) || "1".equals(isEnabledStr));
        }
        
        String isTemplateStr = getCellValueAsString(row.getCell(mapping.getColumnIndex("is_template")));
        if (isTemplateStr != null) {
            testCase.setIsTemplate("true".equalsIgnoreCase(isTemplateStr) || "1".equals(isTemplateStr));
        }
        
        testCase.setVersion(getCellValueAsString(row.getCell(mapping.getColumnIndex("version"))));
        
        return testCase;
    }

    /**
     * 解析CSV行
     */
    private ImportTestCaseDTO parseCsvLine(String line, ColumnMapping mapping, int rowNumber) {
        String[] values = parseCsvLine(line);
        ImportTestCaseDTO testCase = new ImportTestCaseDTO();
        testCase.setRowNumber(rowNumber);
        
        // 解析各个字段
        testCase.setCaseCode(getValue(values, mapping.getColumnIndex("case_code")));
        testCase.setName(getValue(values, mapping.getColumnIndex("name")));
        testCase.setDescription(getValue(values, mapping.getColumnIndex("description")));
        testCase.setPriority(getValue(values, mapping.getColumnIndex("priority")));
        testCase.setSeverity(getValue(values, mapping.getColumnIndex("severity")));
        
        // 解析标签
        String tagsStr = getValue(values, mapping.getColumnIndex("tags"));
        if (tagsStr != null && !tagsStr.trim().isEmpty()) {
            testCase.setTags(parseTags(tagsStr));
        }
        
        testCase.setPreConditions(getValue(values, mapping.getColumnIndex("pre_conditions")));
        testCase.setTestSteps(getValue(values, mapping.getColumnIndex("test_steps")));
        testCase.setRequestOverride(getValue(values, mapping.getColumnIndex("request_override")));
        
        // 解析数字字段
        String expectedStatusStr = getValue(values, mapping.getColumnIndex("expected_http_status"));
        if (expectedStatusStr != null && !expectedStatusStr.trim().isEmpty()) {
            try {
                testCase.setExpectedHttpStatus(Integer.parseInt(expectedStatusStr));
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }
        
        testCase.setExpectedResponseBody(getValue(values, mapping.getColumnIndex("expected_response_body")));
        testCase.setAssertions(getValue(values, mapping.getColumnIndex("assertions")));
        testCase.setExtractors(getValue(values, mapping.getColumnIndex("extractors")));
        testCase.setValidators(getValue(values, mapping.getColumnIndex("validators")));
        
        // 解析布尔字段
        String isEnabledStr = getValue(values, mapping.getColumnIndex("is_enabled"));
        if (isEnabledStr != null) {
            testCase.setIsEnabled("true".equalsIgnoreCase(isEnabledStr) || "1".equals(isEnabledStr));
        }
        
        String isTemplateStr = getValue(values, mapping.getColumnIndex("is_template"));
        if (isTemplateStr != null) {
            testCase.setIsTemplate("true".equalsIgnoreCase(isTemplateStr) || "1".equals(isTemplateStr));
        }
        
        testCase.setVersion(getValue(values, mapping.getColumnIndex("version")));
        
        return testCase;
    }

    /**
     * 解析CSV行（处理引号）
     */
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // 转义的引号
                    current.append('"');
                    i++; // 跳过下一个引号
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    /**
     * 解析标签字符串
     */
    private List<String> parseTags(String tagsStr) {
        List<String> tags = new ArrayList<>();
        if (tagsStr != null && !tagsStr.trim().isEmpty()) {
            // 移除引号
            tagsStr = tagsStr.replaceAll("^\"|\"$", "");
            String[] tagArray = tagsStr.split(",");
            for (String tag : tagArray) {
                String trimmedTag = tag.trim();
                if (!trimmedTag.isEmpty()) {
                    tags.add(trimmedTag);
                }
            }
        }
        return tags;
    }

    /**
     * 获取单元格值作为字符串
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 获取数组中的值
     */
    private String getValue(String[] values, int index) {
        if (index >= 0 && index < values.length) {
            return values[index];
        }
        return null;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * 列映射类
     */
    private static class ColumnMapping {
        private final java.util.Map<String, Integer> columnIndexMap = new java.util.HashMap<>();

        public void setColumnIndex(String columnName, int index) {
            columnIndexMap.put(columnName, index);
        }

        public int getColumnIndex(String columnName) {
            return columnIndexMap.getOrDefault(columnName, -1);
        }
    }
}
