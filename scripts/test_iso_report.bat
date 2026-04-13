@echo off
chcp 65001 >nul
echo ========================================
echo   ISO标准企业级报告测试脚本
echo ========================================
echo.

set REPORT_ID=196
set BASE_URL=http://localhost:8080/api/reports

echo [1] 测试ISO标准企业级报告导出
echo URL: %BASE_URL%/%REPORT_ID%/export/iso
echo.
curl -X GET "%BASE_URL%/%REPORT_ID%/export/iso?locale=zh_CN" ^
  -H "Content-Type: application/json" ^
  -o "ISO企业级测试报告_%REPORT_ID%.html" ^
  -w "\n状态码: %%{http_code}\n文件大小: %%{size_download} bytes\n耗时: %%{time_total}s\n"

echo.
echo ========================================
if exist "ISO企业级测试报告_%REPORT_ID%.html" (
    echo ✓ 报告导出成功！
    echo 文件位置: ISO企业级测试报告_%REPORT_ID%.html
    echo.
    echo 正在打开报告...
    start "" "ISO企业级测试报告_%REPORT_ID%.html"
) else (
    echo ✗ 报告导出失败！
)
echo ========================================
echo.

pause

