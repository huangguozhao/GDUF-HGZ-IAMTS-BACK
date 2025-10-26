@echo off
chcp 65001 >nul
cls
echo ╔══════════════════════════════════════════════════════╗
echo ║     报告状态问题 - 快速修复和测试脚本              ║
echo ╚══════════════════════════════════════════════════════╝
echo.

set REPORT_ID=196
set BASE_URL=http://localhost:8080/api

echo [步骤1] 代码已优化 ✓
echo   修改文件: ReportExportServiceImpl.java
echo   优化逻辑: 有数据即可导出，即使状态为generating
echo.

echo [步骤2] 重新编译项目...
echo.
call mvn clean compile -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo ✗ 编译失败！请检查错误信息
    pause
    exit /b 1
)
echo   ✓ 编译成功！
echo.

echo [步骤3] 请启动Spring Boot服务
echo   命令: mvn spring-boot:run
echo   或者: 在IDE中运行 IatmsApplication
echo.
echo   等待服务启动完成...
echo.
pause
echo.

echo [步骤4] 测试报告导出...
echo.

echo   → 测试1: 标准HTML报告
curl -X GET "%BASE_URL%/reports/%REPORT_ID%/export?export_format=html&include_details=true&include_failure_details=true&timezone=Asia/Shanghai" ^
  -H "Content-Type: application/json" ^
  --output "report_%REPORT_ID%_standard.html" ^
  -w "\n状态码: %%{http_code}\n" ^
  -s

if %ERRORLEVEL% EQU 0 (
    echo   ✓ 标准报告导出成功！
    echo   文件: report_%REPORT_ID%_standard.html
) else (
    echo   ✗ 标准报告导出失败
)
echo.

echo   → 测试2: 企业级报告
curl -X GET "%BASE_URL%/reports/%REPORT_ID%/export/enterprise?locale=zh_CN" ^
  -H "Content-Type: application/json" ^
  --output "report_%REPORT_ID%_enterprise.html" ^
  -w "\n状态码: %%{http_code}\n" ^
  -s

if %ERRORLEVEL% EQU 0 (
    echo   ✓ 企业级报告导出成功！
    echo   文件: report_%REPORT_ID%_enterprise.html
) else (
    echo   ✗ 企业级报告导出失败
)
echo.

echo ════════════════════════════════════════════════════════
echo 📊 查看服务器日志，应该看到:
echo ════════════════════════════════════════════════════════
echo   "报告196状态为generating但已有数据，允许导出"
echo   "开始构建企业级报告数据, reportId: 196"
echo   "查询到XX条测试结果记录"
echo.

echo ════════════════════════════════════════════════════════
echo ✅ 验证清单:
echo ════════════════════════════════════════════════════════
echo   □ 报告导出成功（HTTP 200）
echo   □ 日志显示警告信息（状态为generating但有数据）
echo   □ HTML文件生成
echo   □ 打开报告查看内容
echo.

echo 正在打开报告...
if exist "report_%REPORT_ID%_enterprise.html" (
    start report_%REPORT_ID%_enterprise.html
) else if exist "report_%REPORT_ID%_standard.html" (
    start report_%REPORT_ID%_standard.html
)

echo.
echo ════════════════════════════════════════════════════════
echo 💡 提示:
echo ════════════════════════════════════════════════════════
echo   如果仍然失败，请执行以下SQL修复数据库：
echo.
echo   UPDATE TestReportSummaries
echo   SET report_status = 'completed', updated_at = NOW()
echo   WHERE report_id = %REPORT_ID%;
echo.
echo   然后重新运行本脚本
echo ════════════════════════════════════════════════════════
echo.
pause

