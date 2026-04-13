@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo 测试复制测试用例接口
echo ========================================
echo.

set BASE_URL=http://localhost:8080/api
set CASE_ID=1

echo [步骤1] 复制测试用例
echo.
curl -X POST "%BASE_URL%/testcases/%CASE_ID%/copy" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your-token" ^
  -d "{\"caseCode\":\"TC_AUTH001_001_COPY_20251021\",\"name\":\"正常登录测试(副本)\",\"description\":\"使用正确的用户名和密码登录\"}"
echo.
echo.

echo [步骤2] 复制测试用例 - 使用当前时间戳生成唯一编码
for /f "tokens=1-3 delims=: " %%a in ("%time%") do (
    set hour=%%a
    set minute=%%b
    set second=%%c
)
set hour=%hour: =0%
set timestamp=%date:~0,4%%date:~5,2%%date:~8,2%_%hour%%minute%%second%
echo.
curl -X POST "%BASE_URL%/testcases/%CASE_ID%/copy" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your-token" ^
  -d "{\"caseCode\":\"TC_COPY_%timestamp%\",\"name\":\"测试用例副本_%timestamp%\",\"description\":\"自动生成的测试副本\"}"
echo.
echo.

echo [步骤3] 测试错误场景 - 用例不存在
echo.
curl -X POST "%BASE_URL%/testcases/999999/copy" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your-token" ^
  -d "{\"caseCode\":\"TC_NOT_EXIST\",\"name\":\"不存在的用例\"}"
echo.
echo.

echo [步骤4] 测试错误场景 - 编码已存在
echo.
curl -X POST "%BASE_URL%/testcases/%CASE_ID%/copy" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your-token" ^
  -d "{\"caseCode\":\"TC_AUTH001_001\",\"name\":\"重复编码测试\"}"
echo.
echo.

echo [步骤5] 测试错误场景 - 缺少必填参数
echo.
curl -X POST "%BASE_URL%/testcases/%CASE_ID%/copy" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your-token" ^
  -d "{\"caseCode\":\"\"}"
echo.
echo.

echo ========================================
echo 测试完成
echo ========================================
pause

