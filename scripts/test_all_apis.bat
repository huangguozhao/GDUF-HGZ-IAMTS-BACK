@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

REM TestExecutionController 接口测试脚本（Windows版本）
REM 用于快速测试所有接口是否可用

set BASE_URL=http://localhost:8080/api
set TOTAL=0
set SUCCESS=0
set FAILED=0

echo ======================================
echo TestExecutionController 接口测试
echo ======================================
echo.

echo ========== 测试用例执行相关接口 ==========
echo.

REM 1. 执行单个测试用例
call :test_api "执行单个测试用例" "POST" "%BASE_URL%/test-cases/1/execute" "{\"environment\":\"test\",\"timeout\":30000}"

REM 2. 异步执行测试用例
call :test_api "异步执行测试用例" "POST" "%BASE_URL%/test-cases/1/execute-async" "{\"environment\":\"test\",\"async\":true}"

REM 3. 查询任务状态
call :test_api "查询任务状态" "GET" "%BASE_URL%/tasks/test-task-id-123/status" ""

REM 4. 取消任务执行
call :test_api "取消任务执行" "POST" "%BASE_URL%/tasks/test-task-id-123/cancel" "{}"

REM 5. 获取执行结果详情
call :test_api "获取执行结果详情" "GET" "%BASE_URL%/test-results/1" ""

REM 6. 获取执行日志
call :test_api "获取执行日志" "GET" "%BASE_URL%/test-results/1/logs" ""

REM 7. 生成测试报告
call :test_api "生成测试报告" "POST" "%BASE_URL%/test-results/1/report" "{}"

echo.
echo ========== 模块执行相关接口 ==========
echo.

REM 8. 执行模块测试（同步）
call :test_api "执行模块测试（同步）" "POST" "%BASE_URL%/modules/1/execute" "{\"environment\":\"test\",\"concurrency\":5}"

REM 9. 异步执行模块测试
call :test_api "异步执行模块测试" "POST" "%BASE_URL%/modules/1/execute-async" "{\"environment\":\"test\",\"async\":true}"

REM 10. 查询模块任务状态
call :test_api "查询模块任务状态" "GET" "%BASE_URL%/module-tasks/module-task-123/status" ""

REM 11. 取消模块任务执行
call :test_api "取消模块任务执行" "POST" "%BASE_URL%/module-tasks/module-task-123/cancel" "{}"

echo.
echo ========== 项目执行相关接口 ==========
echo.

REM 12. 执行项目测试
call :test_api "执行项目测试" "POST" "%BASE_URL%/projects/1/execute" "{\"environment\":\"test\",\"concurrency\":10}"

REM 13. 异步执行项目测试
call :test_api "异步执行项目测试" "POST" "%BASE_URL%/projects/1/execute-async" "{\"environment\":\"test\",\"async\":true}"

REM 14. 查询项目任务状态
call :test_api "查询项目任务状态" "GET" "%BASE_URL%/project-tasks/project-task-123/status" ""

REM 15. 取消项目任务执行
call :test_api "取消项目任务执行" "POST" "%BASE_URL%/project-tasks/project-task-123/cancel" "{}"

echo.
echo ========== 接口执行相关接口 ==========
echo.

REM 16. 执行接口测试
call :test_api "执行接口测试" "POST" "%BASE_URL%/apis/1/execute" "{\"environment\":\"test\",\"concurrency\":3}"

REM 17. 异步执行接口测试
call :test_api "异步执行接口测试" "POST" "%BASE_URL%/apis/1/execute-async" "{\"environment\":\"test\",\"async\":true}"

REM 18. 查询接口任务状态
call :test_api "查询接口任务状态" "GET" "%BASE_URL%/api-tasks/api-task-123/status" ""

REM 19. 取消接口任务执行
call :test_api "取消接口任务执行" "POST" "%BASE_URL%/api-tasks/api-task-123/cancel" "{}"

echo.
echo ========== 测试套件执行相关接口 ==========
echo.

REM 20. 执行测试套件
call :test_api "执行测试套件" "POST" "%BASE_URL%/test-suites/1/execute" "{\"environment\":\"test\",\"concurrency\":5}"

REM 21. 异步执行测试套件
call :test_api "异步执行测试套件" "POST" "%BASE_URL%/test-suites/1/execute-async" "{\"environment\":\"test\",\"async\":true}"

REM 22. 查询测试套件任务状态
call :test_api "查询测试套件任务状态" "GET" "%BASE_URL%/suite-tasks/suite-task-123/status" ""

REM 23. 取消测试套件任务执行
call :test_api "取消测试套件任务执行" "POST" "%BASE_URL%/suite-tasks/suite-task-123/cancel" "{}"

echo.
echo ========== 测试结果查询相关接口 ==========
echo.

REM 24. 分页获取测试结果列表（无参数）
call :test_api "获取测试结果列表（无参数）" "GET" "%BASE_URL%/test-results" ""

REM 25. 分页获取测试结果列表（带分页）
call :test_api "获取测试结果列表（带分页）" "GET" "%BASE_URL%/test-results?page=1&page_size=10" ""

REM 26. 查询失败的测试结果
call :test_api "查询失败的测试结果" "GET" "%BASE_URL%/test-results?status=failed&page=1&page_size=20" ""

REM 27. 按任务类型查询
call :test_api "按任务类型查询" "GET" "%BASE_URL%/test-results?task_type=test_case&ref_id=101" ""

REM 28. 复杂条件查询
call :test_api "复杂条件查询" "GET" "%BASE_URL%/test-results?task_type=test_case&status=passed&priority=P0,P1&environment=test&sort_by=start_time&sort_order=desc" ""

REM 29. 关键字搜索
call :test_api "关键字搜索" "GET" "%BASE_URL%/test-results?search_keyword=登录&page=1&page_size=10" ""

echo.
echo ======================================
echo 测试结果统计
echo ======================================
echo 总计: %TOTAL% 个接口
echo 成功: %SUCCESS% 个
echo 失败: %FAILED% 个
echo.

if %FAILED% == 0 (
    echo [成功] 所有接口测试通过！
    exit /b 0
) else (
    echo [失败] 有 %FAILED% 个接口测试失败，请检查！
    exit /b 1
)

REM 测试函数
:test_api
set /a TOTAL+=1
set NAME=%~1
set METHOD=%~2
set URL=%~3
set DATA=%~4

echo [测试 %TOTAL%] %NAME%
echo 请求: %METHOD% %URL%

if "%DATA%"=="" (
    REM GET 请求
    curl -s -o response.tmp -w "%%{http_code}" -X %METHOD% "%URL%" > httpcode.tmp
) else (
    REM POST/PUT/DELETE 请求
    curl -s -o response.tmp -w "%%{http_code}" -X %METHOD% "%URL%" -H "Content-Type: application/json" -d "%DATA%" > httpcode.tmp
)

set /p HTTP_CODE=<httpcode.tmp

if "%HTTP_CODE%"=="200" (
    echo [成功] HTTP %HTTP_CODE%
    set /a SUCCESS+=1
) else if "%HTTP_CODE%"=="201" (
    echo [成功] HTTP %HTTP_CODE%
    set /a SUCCESS+=1
) else (
    echo [失败] HTTP %HTTP_CODE%
    set /a FAILED+=1
)

REM 显示响应体（前200字符）
set /p RESPONSE=<response.tmp
if defined RESPONSE (
    echo 响应: !RESPONSE:~0,200!...
)

echo.
goto :eof

