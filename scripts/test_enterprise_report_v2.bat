@echo off
chcp 65001 >nul
echo ========================================
echo 测试企业级报告V2（使用真实数据）
echo ========================================
echo.

set BASE_URL=http://localhost:8080/api
set REPORT_ID=3

echo [提示] 请确保已启动Spring Boot服务
echo.
pause
echo.

echo [测试] 导出企业级报告V2（包含完整失败详情）
echo.
curl -X GET "%BASE_URL%/reports/%REPORT_ID%/export/enterprise?locale=zh_CN" ^
  -H "Content-Type: application/json" ^
  --output "enterprise_report_v2.html" ^
  -v

echo.
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo ✓ 企业级报告V2导出成功！
    echo ========================================
    echo.
    echo 文件保存为: enterprise_report_v2.html
    echo.
    echo 📋 验证清单:
    echo   □ 报告头部信息完整
    echo   □ KPI指标使用真实数据
    echo   □ 缺陷分布图表正确
    echo   □ 缺陷趋势显示真实日期
    echo   □ 失败用例卡片完整
    echo   □ 点击失败用例可展开
    echo   □ ⭐ 展开后显示错误消息
    echo   □ ⭐ 展开后显示错误类型
    echo   □ ⭐ 展开后显示堆栈跟踪
    echo   □ ⭐ 展开后显示执行环境
    echo.
    echo 📊 查看服务器日志，应该看到:
    echo   - "查询到XX条测试结果记录"
    echo   - "其中失败/异常用例数: X"
    echo   - "示例失败用例: caseName=..."
    echo   - "KPI指标: passRate=..."
    echo.
    echo 正在打开报告...
    start enterprise_report_v2.html
) else (
    echo.
    echo ========================================
    echo ✗ 企业级报告V2导出失败！
    echo ========================================
    echo.
    echo 请检查:
    echo   1. Spring Boot服务是否已启动
    echo   2. 端口8080是否可用
    echo   3. 报告ID=%REPORT_ID% 是否存在
    echo.
)

echo.
pause

