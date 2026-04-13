@echo off
echo ========================================
echo 测试模块统计接口（已修复）
echo ========================================
echo.

echo [测试1] 查询模块1（认证模块）的统计信息
curl -X GET "http://localhost:8080/api/modules/1/statistics" ^
  -H "Authorization: Bearer test-token-123" ^
  -H "Accept: application/json"
echo.
echo.

echo [测试2] 查询模块2（用户模块）的统计信息
curl -X GET "http://localhost:8080/api/modules/2/statistics" ^
  -H "Authorization: Bearer test-token-123" ^
  -H "Accept: application/json"
echo.
echo.

echo ========================================
echo 测试完成
echo ========================================
pause


