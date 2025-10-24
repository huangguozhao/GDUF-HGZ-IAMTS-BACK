@echo off
chcp 65001 >nul
echo ====================================
echo 测试 API接口 CRUD功能
echo ====================================
echo.

set BASE_URL=http://localhost:8080/api
set TOKEN=YOUR_TOKEN_HERE

echo 1. 创建接口
echo ------------------------------------
curl -X POST "%BASE_URL%/apis" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"moduleId\":1,\"name\":\"测试接口\",\"method\":\"GET\",\"path\":\"/api/test\",\"baseUrl\":\"http://localhost:8080\",\"description\":\"测试用接口\",\"status\":\"active\"}"
echo.
echo.

echo 2. 查询接口列表（按模块）
echo ------------------------------------
curl -X GET "%BASE_URL%/apis?moduleId=1&page=1&pageSize=10" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 3. 查询接口列表（包含统计信息）
echo ------------------------------------
curl -X GET "%BASE_URL%/apis?moduleId=1&includeStatistics=true&page=1&pageSize=5" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 4. 查询单个接口（请先将{apiId}替换为实际ID）
echo ------------------------------------
REM curl -X GET "%BASE_URL%/apis/1" ^
REM   -H "Authorization: Bearer %TOKEN%"
echo （跳过，需要实际API ID）
echo.

echo 5. 更新接口（请先将{apiId}替换为实际ID）
echo ------------------------------------
REM curl -X PUT "%BASE_URL%/apis/1" ^
REM   -H "Content-Type: application/json" ^
REM   -H "Authorization: Bearer %TOKEN%" ^
REM   -d "{\"name\":\"更新后的接口\",\"description\":\"已更新\",\"version\":\"2.0\"}"
echo （跳过，需要实际API ID）
echo.

echo 6. 测试查询条件（按HTTP方法）
echo ------------------------------------
curl -X GET "%BASE_URL%/apis?method=GET&page=1&pageSize=5" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 7. 测试查询条件（按状态）
echo ------------------------------------
curl -X GET "%BASE_URL%/apis?status=active&page=1&pageSize=5" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 8. 测试关键字搜索
echo ------------------------------------
curl -X GET "%BASE_URL%/apis?searchKeyword=login&page=1&pageSize=5" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo ====================================
echo 测试完成
echo ====================================
pause

