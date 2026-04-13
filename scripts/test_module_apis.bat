@echo off
echo 测试项目模块列表接口
echo.

echo 1. 测试获取项目1的模块列表（平铺结构）
curl -X GET "http://localhost:8080/projects/1/modules" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json"
echo.
echo.

echo 2. 测试获取项目1的模块列表（树形结构）
curl -X GET "http://localhost:8080/projects/1/modules?structure=tree" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json"
echo.
echo.

echo 3. 测试获取项目1的模块列表（包含统计信息）
curl -X GET "http://localhost:8080/projects/1/modules?include_statistics=true" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json"
echo.
echo.

echo 4. 测试获取项目1的模块列表（按名称排序）
curl -X GET "http://localhost:8080/projects/1/modules?sort_by=name&sort_order=asc" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json"
echo.
echo.

echo 5. 测试获取项目1的模块列表（搜索关键字）
curl -X GET "http://localhost:8080/projects/1/modules?search_keyword=用户" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json"
echo.
echo.

echo 6. 测试获取不存在的项目模块列表
curl -X GET "http://localhost:8080/projects/999/modules" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json"
echo.
echo.

echo 测试完成！
pause
