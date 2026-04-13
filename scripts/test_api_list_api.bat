@echo off
echo 测试获取接口列表接口
echo.

echo 1. 测试获取接口列表（成功）
curl -X GET "http://localhost:8080/modules/1/apis" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 2. 测试获取接口列表（带分页）
curl -X GET "http://localhost:8080/modules/1/apis?page=1&page_size=10" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 3. 测试获取接口列表（按方法过滤）
curl -X GET "http://localhost:8080/modules/1/apis?method=GET" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 4. 测试获取接口列表（按状态过滤）
curl -X GET "http://localhost:8080/modules/1/apis?status=active" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 5. 测试获取接口列表（按标签过滤）
curl -X GET "http://localhost:8080/modules/1/apis?tags=重要&tags=核心" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 6. 测试获取接口列表（按认证类型过滤）
curl -X GET "http://localhost:8080/modules/1/apis?auth_type=bearer" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 7. 测试获取接口列表（关键字搜索）
curl -X GET "http://localhost:8080/modules/1/apis?search_keyword=user" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 8. 测试获取接口列表（包含统计信息）
curl -X GET "http://localhost:8080/modules/1/apis?include_statistics=true" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 9. 测试获取接口列表（包含已删除）
curl -X GET "http://localhost:8080/modules/1/apis?include_deleted=true" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 10. 测试获取接口列表（按名称排序）
curl -X GET "http://localhost:8080/modules/1/apis?sort_by=name&sort_order=asc" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 11. 测试获取接口列表（按创建时间排序）
curl -X GET "http://localhost:8080/modules/1/apis?sort_by=created_at&sort_order=desc" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 12. 测试获取接口列表（组合条件）
curl -X GET "http://localhost:8080/modules/1/apis?method=GET&status=active&tags=重要&include_statistics=true&sort_by=name&sort_order=asc&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 13. 测试获取不存在的模块的接口列表
curl -X GET "http://localhost:8080/modules/999/apis" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 14. 测试获取已删除模块的接口列表
curl -X GET "http://localhost:8080/modules/2/apis" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 15. 测试获取接口列表（权限不足）
curl -X GET "http://localhost:8080/modules/7/apis" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 16. 测试获取接口列表（无认证令牌）
curl -X GET "http://localhost:8080/modules/1/apis"
echo.
echo.

echo 17. 测试获取接口列表（无效的模块ID格式）
curl -X GET "http://localhost:8080/modules/invalid/apis" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 18. 测试获取接口列表（无效的排序字段）
curl -X GET "http://localhost:8080/modules/1/apis?sort_by=invalid_field" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 19. 测试获取接口列表（无效的排序顺序）
curl -X GET "http://localhost:8080/modules/1/apis?sort_order=invalid_order" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 20. 测试获取接口列表（无效的分页参数）
curl -X GET "http://localhost:8080/modules/1/apis?page=0&page_size=0" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 21. 测试获取接口列表（超大的分页参数）
curl -X GET "http://localhost:8080/modules/1/apis?page_size=1000" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 22. 测试获取接口列表（空的关键字搜索）
curl -X GET "http://localhost:8080/modules/1/apis?search_keyword=" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 23. 测试获取接口列表（空的标签过滤）
curl -X GET "http://localhost:8080/modules/1/apis?tags=" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 24. 测试获取接口列表（复杂的关键字搜索）
curl -X GET "http://localhost:8080/modules/1/apis?search_keyword=用户管理" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 测试完成！
echo.
echo 测试说明：
echo - 模块1：普通模块，应该查询成功
echo - 模块2：已删除的模块，应该返回"模块已被删除"
echo - 模块7：非创建者访问，应该返回"权限不足，无法查看接口列表"
echo - 模块999：不存在的模块，应该返回"模块不存在"
echo - 各种过滤条件：方法、状态、标签、认证类型、关键字搜索
echo - 分页和排序：支持多种排序字段和排序顺序
echo - 统计信息：包含接口统计摘要
echo - 参数验证：无效参数的处理
pause
