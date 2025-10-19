@echo off
echo 测试报告导出功能API
echo.

echo 1. 测试导出Excel格式报告
curl -X GET "http://localhost:8080/api/reports/1001/export?export_format=excel" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ^
     --output "test_report_1001.xlsx"
echo.
echo.

echo 2. 测试导出CSV格式报告
curl -X GET "http://localhost:8080/api/reports/1001/export?export_format=csv" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Accept: text/csv" ^
     --output "test_report_1001.csv"
echo.
echo.

echo 3. 测试导出JSON格式报告
curl -X GET "http://localhost:8080/api/reports/1001/export?export_format=json" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Accept: application/json" ^
     --output "test_report_1001.json"
echo.
echo.

echo 4. 测试导出Excel格式报告（不包含详细信息）
curl -X GET "http://localhost:8080/api/reports/1001/export?export_format=excel&include_details=false" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ^
     --output "test_report_1001_summary.xlsx"
echo.
echo.

echo 5. 测试导出Excel格式报告（包含附件信息）
curl -X GET "http://localhost:8080/api/reports/1001/export?export_format=excel&include_attachments=true" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ^
     --output "test_report_1001_with_attachments.xlsx"
echo.
echo.

echo 6. 测试导出Excel格式报告（不包含失败详情）
curl -X GET "http://localhost:8080/api/reports/1001/export?export_format=excel&include_failure_details=false" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ^
     --output "test_report_1001_no_failures.xlsx"
echo.
echo.

echo 7. 测试导出Excel格式报告（指定时区）
curl -X GET "http://localhost:8080/api/reports/1001/export?export_format=excel&timezone=Asia/Shanghai" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ^
     --output "test_report_1001_cst.xlsx"
echo.
echo.

echo 8. 测试导出不存在的报告（应该返回404）
curl -X GET "http://localhost:8080/api/reports/99999/export?export_format=excel" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
echo.
echo.

echo 9. 测试导出不支持的格式（应该返回400）
curl -X GET "http://localhost:8080/api/reports/1001/export?export_format=pdf" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Accept: application/pdf"
echo.
echo.

echo 10. 测试导出生成中的报告（应该返回409）
curl -X GET "http://localhost:8080/api/reports/1002/export?export_format=excel" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
echo.
echo.

echo 测试完成！
echo 导出的文件：
dir *.xlsx *.csv *.json
pause
