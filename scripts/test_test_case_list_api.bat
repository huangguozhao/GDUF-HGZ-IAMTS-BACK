@echo off
echo 测试分页获取测试用例列表API
echo.

echo 1. 测试基本分页查询
curl -X GET "http://localhost:8080/testcases?page=1&page_size=10" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 2. 测试按接口ID过滤
curl -X GET "http://localhost:8080/testcases?api_id=101&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 3. 测试按模块ID过滤
curl -X GET "http://localhost:8080/testcases?module_id=5&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 4. 测试按项目ID过滤
curl -X GET "http://localhost:8080/testcases?project_id=1&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 5. 测试按用例名称模糊查询
curl -X GET "http://localhost:8080/testcases?name=登录&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 6. 测试按用例编码精确查询
curl -X GET "http://localhost:8080/testcases?case_code=TC-API-101-001&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 7. 测试按优先级过滤
curl -X GET "http://localhost:8080/testcases?priority=P0&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 8. 测试按严重程度过滤
curl -X GET "http://localhost:8080/testcases?severity=high&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 9. 测试按状态过滤
curl -X GET "http://localhost:8080/testcases?status=active&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 10. 测试按模板用例过滤
curl -X GET "http://localhost:8080/testcases?is_template=false&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 11. 测试按创建人过滤
curl -X GET "http://localhost:8080/testcases?created_by=123&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 12. 测试关键字搜索
curl -X GET "http://localhost:8080/testcases?search_keyword=用户&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 13. 测试包含已删除用例
curl -X GET "http://localhost:8080/testcases?include_deleted=true&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 14. 测试按名称排序
curl -X GET "http://localhost:8080/testcases?sort_by=name&sort_order=asc&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 15. 测试按优先级排序
curl -X GET "http://localhost:8080/testcases?sort_by=priority&sort_order=desc&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 16. 测试按创建时间排序
curl -X GET "http://localhost:8080/testcases?sort_by=created_at&sort_order=desc&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 17. 测试按更新时间排序
curl -X GET "http://localhost:8080/testcases?sort_by=updated_at&sort_order=desc&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 18. 测试多条件组合查询
curl -X GET "http://localhost:8080/testcases?project_id=1&module_id=5&priority=P0&severity=high&status=active&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 19. 测试大分页
curl -X GET "http://localhost:8080/testcases?page=1&page_size=100" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 20. 测试超大分页（应该失败）
curl -X GET "http://localhost:8080/testcases?page=1&page_size=101" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 21. 测试无效页码
curl -X GET "http://localhost:8080/testcases?page=0&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 22. 测试无效分页大小
curl -X GET "http://localhost:8080/testcases?page=1&page_size=0" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 23. 测试无效排序字段
curl -X GET "http://localhost:8080/testcases?sort_by=invalid_field&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 24. 测试无效排序顺序
curl -X GET "http://localhost:8080/testcases?sort_order=invalid_order&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 25. 测试无效优先级
curl -X GET "http://localhost:8080/testcases?priority=P5&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 26. 测试无效严重程度
curl -X GET "http://localhost:8080/testcases?severity=invalid&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 27. 测试无效状态
curl -X GET "http://localhost:8080/testcases?status=invalid&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 28. 测试无认证令牌
curl -X GET "http://localhost:8080/testcases?page=1&page_size=20"
echo.
echo.

echo 29. 测试过期令牌
curl -X GET "http://localhost:8080/testcases?page=1&page_size=20" ^
  -H "Authorization: Bearer expired_token_here"
echo.
echo.

echo 30. 测试无效令牌
curl -X GET "http://localhost:8080/testcases?page=1&page_size=20" ^
  -H "Authorization: Bearer invalid_token_here"
echo.
echo.

echo 31. 测试错误的请求方法
curl -X POST "http://localhost:8080/testcases?page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 32. 测试错误的请求路径
curl -X GET "http://localhost:8080/testcase?page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 33. 测试空查询参数
curl -X GET "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 34. 测试特殊字符查询
curl -X GET "http://localhost:8080/testcases?name=测试%20用例&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 35. 测试长查询参数
curl -X GET "http://localhost:8080/testcases?name=这是一个非常长的测试用例名称用于测试系统对长名称的处理能力&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 36. 测试边界值查询
curl -X GET "http://localhost:8080/testcases?page=1&page_size=1" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 37. 测试最大页码
curl -X GET "http://localhost:8080/testcases?page=999999&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 38. 测试负数页码
curl -X GET "http://localhost:8080/testcases?page=-1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 39. 测试负数分页大小
curl -X GET "http://localhost:8080/testcases?page=1&page_size=-1" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 40. 测试并发查询
curl -X GET "http://localhost:8080/testcases?page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here" &
curl -X GET "http://localhost:8080/testcases?page=2&page_size=20" ^
  -H "Authorization: Bearer your_token_here" &
wait
echo.
echo.

echo 41. 测试压力查询
for /L %%i in (1,1,10) do (
    curl -X GET "http://localhost:8080/testcases?page=%%i&page_size=10" ^
      -H "Authorization: Bearer your_token_here"
    echo.
)
echo.

echo 42. 测试不同排序组合
curl -X GET "http://localhost:8080/testcases?sort_by=name&sort_order=asc&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?sort_by=name&sort_order=desc&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?sort_by=case_code&sort_order=asc&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?sort_by=case_code&sort_order=desc&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 43. 测试不同优先级组合
curl -X GET "http://localhost:8080/testcases?priority=P0&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?priority=P1&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?priority=P2&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?priority=P3&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 44. 测试不同严重程度组合
curl -X GET "http://localhost:8080/testcases?severity=critical&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?severity=high&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?severity=medium&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?severity=low&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 45. 测试不同状态组合
curl -X GET "http://localhost:8080/testcases?status=active&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?status=inactive&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 46. 测试不同模板状态组合
curl -X GET "http://localhost:8080/testcases?is_template=true&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?is_template=false&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 47. 测试不同删除状态组合
curl -X GET "http://localhost:8080/testcases?include_deleted=true&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?include_deleted=false&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 48. 测试复杂组合查询
curl -X GET "http://localhost:8080/testcases?project_id=1&module_id=5&api_id=101&priority=P0&severity=high&status=active&is_template=false&created_by=123&search_keyword=登录&sort_by=created_at&sort_order=desc&page=1&page_size=20" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 49. 测试统计摘要功能
curl -X GET "http://localhost:8080/testcases?page=1&page_size=5" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 50. 测试分页边界
curl -X GET "http://localhost:8080/testcases?page=1&page_size=1" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?page=2&page_size=1" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X GET "http://localhost:8080/testcases?page=3&page_size=1" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 测试完成！
echo.
echo 测试说明：
echo - 基本分页查询：测试默认分页参数
echo - 各种过滤条件：测试单个过滤条件
echo - 多条件组合：测试多个过滤条件组合
echo - 排序功能：测试不同排序字段和顺序
echo - 分页参数：测试分页大小限制和边界值
echo - 参数验证：测试无效参数的处理
echo - 认证权限：测试认证和权限控制
echo - 并发压力：测试并发查询和压力测试
echo - 统计摘要：测试统计摘要功能
echo - 边界测试：测试各种边界情况
pause
