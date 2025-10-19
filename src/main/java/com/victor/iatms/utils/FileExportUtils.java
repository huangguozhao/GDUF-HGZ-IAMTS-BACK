package com.victor.iatms.utils;

import com.victor.iatms.entity.dto.ExportTestCaseDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 文件导出工具类
 */
@Component
public class FileExportUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 导出为Excel格式
     * @param testCases 测试用例列表
     * @param fields 要导出的字段
     * @return Excel文件字节数组
     * @throws IOException IO异常
     */
    public byte[] exportToExcel(List<ExportTestCaseDTO> testCases, List<String> fields) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        
        try {
            // 创建主工作表
            Sheet mainSheet = workbook.createSheet("测试用例");
            createMainSheet(mainSheet, testCases, fields);
            
            // 创建数据字典工作表
            Sheet dictSheet = workbook.createSheet("数据字典");
            createDictionarySheet(dictSheet);
            
            // 创建导入模板工作表
            Sheet templateSheet = workbook.createSheet("导入模板");
            createTemplateSheet(templateSheet, fields);
            
            // 写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } finally {
            workbook.close();
        }
    }

    /**
     * 导出为CSV格式
     * @param testCases 测试用例列表
     * @param fields 要导出的字段
     * @return CSV文件字节数组
     * @throws IOException IO异常
     */
    public byte[] exportToCsv(List<ExportTestCaseDTO> testCases, List<String> fields) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            // 写入BOM，确保Excel正确识别UTF-8编码
            writer.write('\ufeff');
            
            // 写入表头
            writeCsvHeader(writer, fields);
            
            // 写入数据行
            for (ExportTestCaseDTO testCase : testCases) {
                writeCsvRow(writer, testCase, fields);
            }
            
            writer.flush();
            return outputStream.toByteArray();
        }
    }

    /**
     * 创建主工作表
     */
    private void createMainSheet(Sheet sheet, List<ExportTestCaseDTO> testCases, List<String> fields) {
        // 创建表头样式
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        int colIndex = 0;
        for (String field : fields) {
            Cell cell = headerRow.createCell(colIndex++);
            cell.setCellValue(getFieldDisplayName(field));
            cell.setCellStyle(headerStyle);
        }
        
        // 创建数据行
        int rowIndex = 1;
        for (ExportTestCaseDTO testCase : testCases) {
            Row dataRow = sheet.createRow(rowIndex++);
            colIndex = 0;
            
            for (String field : fields) {
                Cell cell = dataRow.createCell(colIndex++);
                setCellValue(cell, testCase, field);
            }
        }
        
        // 自动调整列宽
        for (int i = 0; i < fields.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 创建数据字典工作表
     */
    private void createDictionarySheet(Sheet sheet) {
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("字段名");
        headerRow.createCell(1).setCellValue("字段说明");
        headerRow.createCell(2).setCellValue("数据类型");
        headerRow.createCell(3).setCellValue("可选值");
        
        for (int i = 0; i < 4; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        // 添加字段说明
        String[][] fieldInfo = {
            {"case_code", "用例编码", "字符串", "必填，接口内唯一"},
            {"name", "用例名称", "字符串", "必填"},
            {"description", "用例描述", "字符串", "可选"},
            {"priority", "优先级", "枚举", "P0, P1, P2, P3"},
            {"severity", "严重程度", "枚举", "critical, high, medium, low"},
            {"tags", "标签", "字符串数组", "多个标签用逗号分隔"},
            {"pre_conditions", "前置条件", "JSON字符串", "可选"},
            {"test_steps", "测试步骤", "JSON字符串", "可选"},
            {"request_override", "请求参数覆盖", "JSON字符串", "可选"},
            {"expected_http_status", "预期HTTP状态码", "整数", "可选"},
            {"expected_response_schema", "预期响应Schema", "JSON字符串", "可选"},
            {"expected_response_body", "预期响应体", "字符串", "可选"},
            {"assertions", "断言规则", "JSON字符串", "可选"},
            {"extractors", "响应提取规则", "JSON字符串", "可选"},
            {"validators", "验证器配置", "JSON字符串", "可选"},
            {"is_enabled", "是否启用", "布尔值", "true/false"},
            {"is_template", "是否为模板", "布尔值", "true/false"},
            {"version", "版本号", "字符串", "可选，默认1.0"}
        };
        
        int rowIndex = 1;
        for (String[] info : fieldInfo) {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < info.length; i++) {
                row.createCell(i).setCellValue(info[i]);
            }
        }
        
        // 自动调整列宽
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 创建导入模板工作表
     */
    private void createTemplateSheet(Sheet sheet, List<String> fields) {
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        int colIndex = 0;
        for (String field : fields) {
            Cell cell = headerRow.createCell(colIndex++);
            cell.setCellValue(getFieldDisplayName(field));
            cell.setCellStyle(headerStyle);
        }
        
        // 创建示例数据行
        Row exampleRow = sheet.createRow(1);
        colIndex = 0;
        for (String field : fields) {
            Cell cell = exampleRow.createCell(colIndex++);
            cell.setCellValue(getFieldExampleValue(field));
        }
        
        // 自动调整列宽
        for (int i = 0; i < fields.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    /**
     * 设置单元格值
     */
    private void setCellValue(Cell cell, ExportTestCaseDTO testCase, String field) {
        switch (field) {
            case "case_code":
                cell.setCellValue(testCase.getCaseCode());
                break;
            case "name":
                cell.setCellValue(testCase.getName());
                break;
            case "description":
                cell.setCellValue(testCase.getDescription());
                break;
            case "priority":
                cell.setCellValue(testCase.getPriority());
                break;
            case "severity":
                cell.setCellValue(testCase.getSeverity());
                break;
            case "tags":
                cell.setCellValue(formatTags(testCase.getTags()));
                break;
            case "pre_conditions":
                cell.setCellValue(testCase.getPreConditions());
                break;
            case "test_steps":
                cell.setCellValue(testCase.getTestSteps());
                break;
            case "request_override":
                cell.setCellValue(testCase.getRequestOverride());
                break;
            case "expected_http_status":
                if (testCase.getExpectedHttpStatus() != null) {
                    cell.setCellValue(testCase.getExpectedHttpStatus());
                }
                break;
            case "expected_response_schema":
                cell.setCellValue(testCase.getExpectedResponseSchema());
                break;
            case "expected_response_body":
                cell.setCellValue(testCase.getExpectedResponseBody());
                break;
            case "assertions":
                cell.setCellValue(testCase.getAssertions());
                break;
            case "extractors":
                cell.setCellValue(testCase.getExtractors());
                break;
            case "validators":
                cell.setCellValue(testCase.getValidators());
                break;
            case "is_enabled":
                cell.setCellValue(testCase.getIsEnabled() != null ? testCase.getIsEnabled() : true);
                break;
            case "is_template":
                cell.setCellValue(testCase.getIsTemplate() != null ? testCase.getIsTemplate() : false);
                break;
            case "version":
                cell.setCellValue(testCase.getVersion());
                break;
            case "created_by":
                if (testCase.getCreatedBy() != null) {
                    cell.setCellValue(testCase.getCreatedBy());
                }
                break;
            case "creator_name":
                cell.setCellValue(testCase.getCreatorName());
                break;
            case "created_at":
                if (testCase.getCreatedAt() != null) {
                    cell.setCellValue(testCase.getCreatedAt().format(DATE_TIME_FORMATTER));
                }
                break;
            case "updated_at":
                if (testCase.getUpdatedAt() != null) {
                    cell.setCellValue(testCase.getUpdatedAt().format(DATE_TIME_FORMATTER));
                }
                break;
        }
    }

    /**
     * 写入CSV表头
     */
    private void writeCsvHeader(Writer writer, List<String> fields) throws IOException {
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                writer.write(",");
            }
            writer.write("\"" + getFieldDisplayName(fields.get(i)) + "\"");
        }
        writer.write("\n");
    }

    /**
     * 写入CSV数据行
     */
    private void writeCsvRow(Writer writer, ExportTestCaseDTO testCase, List<String> fields) throws IOException {
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                writer.write(",");
            }
            String value = getFieldValue(testCase, fields.get(i));
            writer.write("\"" + escapeCsvValue(value) + "\"");
        }
        writer.write("\n");
    }

    /**
     * 获取字段值
     */
    private String getFieldValue(ExportTestCaseDTO testCase, String field) {
        switch (field) {
            case "case_code":
                return testCase.getCaseCode();
            case "name":
                return testCase.getName();
            case "description":
                return testCase.getDescription();
            case "priority":
                return testCase.getPriority();
            case "severity":
                return testCase.getSeverity();
            case "tags":
                return formatTags(testCase.getTags());
            case "pre_conditions":
                return testCase.getPreConditions();
            case "test_steps":
                return testCase.getTestSteps();
            case "request_override":
                return testCase.getRequestOverride();
            case "expected_http_status":
                return testCase.getExpectedHttpStatus() != null ? testCase.getExpectedHttpStatus().toString() : "";
            case "expected_response_schema":
                return testCase.getExpectedResponseSchema();
            case "expected_response_body":
                return testCase.getExpectedResponseBody();
            case "assertions":
                return testCase.getAssertions();
            case "extractors":
                return testCase.getExtractors();
            case "validators":
                return testCase.getValidators();
            case "is_enabled":
                return testCase.getIsEnabled() != null ? testCase.getIsEnabled().toString() : "true";
            case "is_template":
                return testCase.getIsTemplate() != null ? testCase.getIsTemplate().toString() : "false";
            case "version":
                return testCase.getVersion();
            case "created_by":
                return testCase.getCreatedBy() != null ? testCase.getCreatedBy().toString() : "";
            case "creator_name":
                return testCase.getCreatorName();
            case "created_at":
                return testCase.getCreatedAt() != null ? testCase.getCreatedAt().format(DATE_TIME_FORMATTER) : "";
            case "updated_at":
                return testCase.getUpdatedAt() != null ? testCase.getUpdatedAt().format(DATE_TIME_FORMATTER) : "";
            default:
                return "";
        }
    }

    /**
     * 格式化标签
     */
    private String formatTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return String.join(",", tags);
    }

    /**
     * 获取字段显示名称
     */
    private String getFieldDisplayName(String field) {
        switch (field) {
            case "case_code":
                return "用例编码";
            case "name":
                return "用例名称";
            case "description":
                return "用例描述";
            case "priority":
                return "优先级";
            case "severity":
                return "严重程度";
            case "tags":
                return "标签";
            case "pre_conditions":
                return "前置条件";
            case "test_steps":
                return "测试步骤";
            case "request_override":
                return "请求参数覆盖";
            case "expected_http_status":
                return "预期HTTP状态码";
            case "expected_response_schema":
                return "预期响应Schema";
            case "expected_response_body":
                return "预期响应体";
            case "assertions":
                return "断言规则";
            case "extractors":
                return "响应提取规则";
            case "validators":
                return "验证器配置";
            case "is_enabled":
                return "是否启用";
            case "is_template":
                return "是否为模板";
            case "version":
                return "版本号";
            case "created_by":
                return "创建人ID";
            case "creator_name":
                return "创建人姓名";
            case "created_at":
                return "创建时间";
            case "updated_at":
                return "更新时间";
            default:
                return field;
        }
    }

    /**
     * 获取字段示例值
     */
    private String getFieldExampleValue(String field) {
        switch (field) {
            case "case_code":
                return "TC-API-101-001";
            case "name":
                return "用户登录-成功场景";
            case "description":
                return "测试成功登录场景";
            case "priority":
                return "P0";
            case "severity":
                return "high";
            case "tags":
                return "冒烟测试,登录功能";
            case "pre_conditions":
                return "用户已注册";
            case "test_steps":
                return "1.输入用户名和密码 2.点击登录按钮";
            case "request_override":
                return "{\"username\":\"test@example.com\"}";
            case "expected_http_status":
                return "200";
            case "expected_response_schema":
                return "{\"type\":\"object\"}";
            case "expected_response_body":
                return "{\"code\":1,\"msg\":\"success\"}";
            case "assertions":
                return "{\"status\":\"equals\",\"value\":200}";
            case "extractors":
                return "{\"token\":\"$.data.token\"}";
            case "validators":
                return "{\"response_time\":\"<1000\"}";
            case "is_enabled":
                return "true";
            case "is_template":
                return "false";
            case "version":
                return "1.0";
            case "created_by":
                return "123";
            case "creator_name":
                return "张三";
            case "created_at":
                return "2024-06-15 10:30:00";
            case "updated_at":
                return "2024-06-15 10:30:00";
            default:
                return "";
        }
    }

    /**
     * 转义CSV值
     */
    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        // 转义双引号
        return value.replace("\"", "\"\"");
    }
}
