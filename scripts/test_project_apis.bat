@echo off
echo ========================================
echo 项目执行管理模块接口测试
echo ========================================

set BASE_URL=http://localhost:8080/api/projects
set TOKEN=your_auth_token_here

echo.
echo 1. 测试分页获取项目列表
echo GET %BASE_URL%
curl -X GET "%BASE_URL%" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"

echo.
echo.
echo 2. 测试按名称搜索项目
echo GET %BASE_URL%?name=电商
curl -X GET "%BASE_URL%?name=电商" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"

echo.
echo.
echo 3. 测试按创建人ID过滤项目
echo GET %BASE_URL%?creator_id=123
curl -X GET "%BASE_URL%?creator_id=123" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"

echo.
echo.
echo 4. 测试包含已删除项目
echo GET %BASE_URL%?include_deleted=true
curl -X GET "%BASE_URL%?include_deleted=true" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"

echo.
echo.
echo 5. 测试按名称排序
echo GET %BASE_URL%?sort_by=name&sort_order=asc
curl -X GET "%BASE_URL%?sort_by=name&sort_order=asc" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"

echo.
echo.
echo 6. 测试分页参数
echo GET %BASE_URL%?page=2&page_size=5
curl -X GET "%BASE_URL%?page=2&page_size=5" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"

echo.
echo.
echo 7. 测试根据ID获取项目详情
echo GET %BASE_URL%/1
curl -X GET "%BASE_URL%/1" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"

echo.
echo.
echo 8. 测试根据项目编码获取项目详情
echo GET %BASE_URL%/code/PROJ-001
curl -X GET "%BASE_URL%/code/PROJ-001" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"

echo.
echo.
echo 9. 测试创建项目
echo POST %BASE_URL%
curl -X POST "%BASE_URL%" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"projectCode\":\"PROJ-TEST-001\",\"name\":\"测试项目\",\"description\":\"这是一个测试项目\",\"createdBy\":1}"

echo.
echo.
echo 10. 测试更新项目
echo PUT %BASE_URL%/1
curl -X PUT "%BASE_URL%/1" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"更新后的项目名称\",\"description\":\"更新后的项目描述\"}"

echo.
echo.
echo 11. 测试检查项目编码是否存在
echo GET %BASE_URL%/check-code?project_code=PROJ-001
curl -X GET "%BASE_URL%/check-code?project_code=PROJ-001" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"

echo.
echo.
echo 12. 测试删除项目
echo DELETE %BASE_URL%/1
curl -X DELETE "%BASE_URL%/1" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json"

echo.
echo.
echo ========================================
echo 测试完成
echo ========================================
pause
