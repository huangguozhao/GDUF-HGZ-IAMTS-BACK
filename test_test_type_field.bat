@echo off
chcp 65001 >nul
echo ====================================
echo 测试 test_type 字段功能
echo ====================================
echo.

REM 设置变量
set BASE_URL=http://localhost:8080/api
set TOKEN=YOUR_TOKEN_HERE

echo 1. 测试创建测试用例（包含testType）
echo ------------------------------------
curl -X POST "%BASE_URL%/test-cases" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"apiId\":1,\"name\":\"性能测试示例\",\"description\":\"测试性能\",\"testType\":\"performance\",\"priority\":\"P1\",\"severity\":\"high\"}"
echo.
echo.

echo 2. 测试按testType查询（查询functional类型）
echo ------------------------------------
curl -X GET "%BASE_URL%/test-cases?testType=functional&page=1&pageSize=5" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 3. 测试按testType查询（查询performance类型）
echo ------------------------------------
curl -X GET "%BASE_URL%/test-cases?testType=performance&page=1&pageSize=5" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 4. 测试组合查询（apiId + testType）
echo ------------------------------------
curl -X GET "%BASE_URL%/test-cases?apiId=1&testType=functional&page=1&pageSize=5" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 5. 查询数据库中test_type字段分布
echo ------------------------------------
mysql -u root -p12345678 iatmsdb -e "SELECT test_type, COUNT(*) as count FROM TestCases WHERE is_deleted = FALSE GROUP BY test_type;"
echo.

echo ====================================
echo 测试完成
echo ====================================
pause

