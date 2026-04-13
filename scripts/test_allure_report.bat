@echo off
chcp 65001 >nul
echo ========================================
echo   Allure风格测试报告 - 快速测试脚本
echo ========================================
echo.

REM 设置变量
set REPORT_ID=196
set BASE_URL=http://localhost:8080/api
set LOCALE=zh_CN
set TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%
set OUTPUT_FILE=Allure测试报告_%TIMESTAMP%.html

echo [1/3] 检查服务状态...
curl -s -o nul -w "HTTP状态码: %%{http_code}\n" %BASE_URL%/reports
if errorlevel 1 (
    echo ❌ 错误: 无法连接到服务，请确保服务已启动
    echo    启动命令: mvn spring-boot:run
    pause
    exit /b 1
)
echo ✓ 服务运行正常
echo.

echo [2/3] 导出Allure风格测试报告...
echo    报告ID: %REPORT_ID%
echo    语言: %LOCALE%
echo    输出文件: %OUTPUT_FILE%
echo.

curl -X GET "%BASE_URL%/reports/%REPORT_ID%/export/allure?locale=%LOCALE%" ^
     -H "Content-Type: application/json" ^
     -o "%OUTPUT_FILE%" ^
     -w "\n下载状态: %%{http_code}\n文件大小: %%{size_download} bytes\n耗时: %%{time_total}s\n"

if errorlevel 1 (
    echo.
    echo ❌ 导出失败，请检查：
    echo    1. 报告ID是否正确
    echo    2. 报告是否包含测试数据
    echo    3. 查看服务日志获取详细错误
    pause
    exit /b 1
)

echo.
echo [3/3] 验证导出的报告...
if exist "%OUTPUT_FILE%" (
    for %%A in ("%OUTPUT_FILE%") do set FILE_SIZE=%%~zA
    if !FILE_SIZE! LSS 1000 (
        echo ❌ 报告文件太小，可能导出失败
        type "%OUTPUT_FILE%"
        pause
        exit /b 1
    )
    echo ✓ 报告导出成功！
    echo.
    echo ========================================
    echo   导出完成
    echo ========================================
    echo.
    echo 📄 报告文件: %OUTPUT_FILE%
    echo 📊 文件大小: !FILE_SIZE! bytes
    echo.
    echo 💡 提示：
    echo    - 双击打开HTML文件查看报告
    echo    - 报告包含详细的测试步骤、HTTP请求/响应、错误堆栈等
    echo    - 推荐使用Chrome、Firefox或Edge浏览器
    echo.
    
    REM 询问是否打开报告
    set /p OPEN_REPORT="是否立即打开报告？(Y/N): "
    if /i "%OPEN_REPORT%"=="Y" (
        start "" "%OUTPUT_FILE%"
        echo ✓ 已在浏览器中打开报告
    )
) else (
    echo ❌ 报告文件未生成
    pause
    exit /b 1
)

echo.
echo ========================================
echo   测试完成
echo ========================================
pause

