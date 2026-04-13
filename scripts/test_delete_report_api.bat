@echo off
echo ========================================
echo 测试报告删除接口
echo ========================================

set BASE_URL=http://localhost:8080
set REPORT_ID=1001

echo.
echo 1. 测试软删除报告（默认）
echo ========================================
curl -X DELETE "%BASE_URL%/reports/%REPORT_ID%" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your_token_here" ^
  -v

echo.
echo.
echo 2. 测试强制删除报告（物理删除）
echo ========================================
curl -X DELETE "%BASE_URL%/reports/%REPORT_ID%?force=true" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your_token_here" ^
  -v

echo.
echo.
echo 3. 测试删除不存在的报告
echo ========================================
curl -X DELETE "%BASE_URL%/reports/99999" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your_token_here" ^
  -v

echo.
echo.
echo 4. 测试删除已删除的报告
echo ========================================
curl -X DELETE "%BASE_URL%/reports/%REPORT_ID%" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your_token_here" ^
  -v

echo.
echo.
echo 5. 测试无权限删除报告
echo ========================================
curl -X DELETE "%BASE_URL%/reports/%REPORT_ID%" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer invalid_token" ^
  -v

echo.
echo.
echo 6. 测试删除有依赖关系的报告
echo ========================================
curl -X DELETE "%BASE_URL%/reports/1002" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your_token_here" ^
  -v

echo.
echo.
echo 7. 测试参数错误（无效的report_id）
echo ========================================
curl -X DELETE "%BASE_URL%/reports/abc" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your_token_here" ^
  -v

echo.
echo.
echo 8. 测试未认证删除报告
echo ========================================
curl -X DELETE "%BASE_URL%/reports/%REPORT_ID%" ^
  -H "Content-Type: application/json" ^
  -v

echo.
echo ========================================
echo 测试完成
echo ========================================
pause
