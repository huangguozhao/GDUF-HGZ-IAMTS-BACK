@echo off
chcp 65001 >nul
echo ========================================
echo 测试企业级报告导出功能
echo ========================================
echo.

set BASE_URL=http://localhost:8080/api
set REPORT_ID=3

echo [1] 导出标准HTML报告（原版）
echo.
curl -X GET "%BASE_URL%/reports/%REPORT_ID%/export?export_format=html&include_details=true&include_attachments=false&include_failure_details=true" ^
  -H "Content-Type: application/json" ^
  --output "standard_report.html"
  
if %ERRORLEVEL% EQU 0 (
    echo ✓ 标准HTML报告导出成功！
    echo 文件保存为: standard_report.html
) else (
    echo ✗ 标准HTML报告导出失败！
)
echo.
echo ----------------------------------------
echo.

echo [2] 导出企业级HTML报告（新版）
echo.
curl -X GET "%BASE_URL%/reports/%REPORT_ID%/export/enterprise?locale=zh_CN" ^
  -H "Content-Type: application/json" ^
  --output "enterprise_report.html"
  
if %ERRORLEVEL% EQU 0 (
    echo ✓ 企业级HTML报告导出成功！
    echo 文件保存为: enterprise_report.html
) else (
    echo ✗ 企业级HTML报告导出失败！
)
echo.
echo ----------------------------------------
echo.

echo [3] 测试完成！
echo.
echo 请打开以下文件查看报告效果：
echo - standard_report.html （标准版报告）
echo - enterprise_report.html （企业级报告）
echo.

pause

