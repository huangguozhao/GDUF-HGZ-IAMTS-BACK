@echo off
echo 测试修改测试用例API
echo.

echo 1. 测试修改测试用例（成功）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"用户登录-成功场景-更新\", \"description\": \"更新后的测试用例描述\", \"priority\": \"P0\", \"severity\": \"critical\", \"tags\": [\"冒烟测试\", \"登录功能\", \"重要\"], \"is_enabled\": true}"
echo.
echo.

echo 2. 测试修改测试用例（部分更新）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"用户登录-成功场景-部分更新\", \"priority\": \"P1\"}"
echo.
echo.

echo 3. 测试修改测试用例（更新用例编码）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"case_code\": \"TC-API-101-UPDATED-001\", \"name\": \"用户登录-更新编码\"}"
echo.
echo.

echo 4. 测试修改测试用例（更新模板）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"template_id\": 1002, \"name\": \"用户登录-更新模板\"}"
echo.
echo.

echo 5. 测试修改测试用例（完整配置更新）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"用户登录-完整配置更新\", \"description\": \"包含完整配置的测试用例更新\", \"priority\": \"P0\", \"severity\": \"critical\", \"tags\": [\"冒烟测试\", \"登录功能\", \"核心功能\"], \"request_override\": {\"request_body\": {\"username\": \"updated_user\", \"password\": \"NewPassword123!\"}}, \"expected_http_status\": 200, \"expected_response_schema\": {\"type\": \"object\", \"properties\": {\"code\": {\"type\": \"number\"}, \"msg\": {\"type\": \"string\"}, \"data\": {\"type\": \"object\", \"properties\": {\"token\": {\"type\": \"string\"}}}}}, \"assertions\": [{\"type\": \"status_code\", \"expected\": 200}, {\"type\": \"json_path\", \"expression\": \"$.code\", \"expected\": 1}, {\"type\": \"response_time\", \"max_time_ms\": 1000}], \"is_enabled\": true}"
echo.
echo.

echo 6. 测试修改测试用例（用例不存在）
curl -X PUT "http://localhost:8080/testcases/9999" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"不存在的用例\", \"description\": \"测试不存在的用例\", \"priority\": \"P2\", \"severity\": \"medium\"}"
echo.
echo.

echo 7. 测试修改测试用例（用例已被删除）
curl -X PUT "http://localhost:8080/testcases/1002" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"已删除的用例\", \"description\": \"测试已删除的用例\", \"priority\": \"P2\", \"severity\": \"medium\"}"
echo.
echo.

echo 8. 测试修改测试用例（用例编码已存在）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"case_code\": \"TC-API-101-002\", \"name\": \"重复编码用例\", \"description\": \"测试重复的用例编码\", \"priority\": \"P2\", \"severity\": \"medium\"}"
echo.
echo.

echo 9. 测试修改测试用例（模板用例不存在）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"template_id\": 9999, \"name\": \"模板不存在用例\", \"description\": \"测试不存在的模板\", \"priority\": \"P2\", \"severity\": \"medium\"}"
echo.
echo.

echo 10. 测试修改测试用例（权限不足）
curl -X PUT "http://localhost:8080/testcases/1003" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"权限不足用例\", \"description\": \"测试权限不足的情况\", \"priority\": \"P2\", \"severity\": \"medium\"}"
echo.
echo.

echo 11. 测试修改测试用例（无效的优先级）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"无效优先级用例\", \"description\": \"测试无效的优先级\", \"priority\": \"P5\", \"severity\": \"medium\"}"
echo.
echo.

echo 12. 测试修改测试用例（无效的严重程度）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"无效严重程度用例\", \"description\": \"测试无效的严重程度\", \"priority\": \"P2\", \"severity\": \"invalid\"}"
echo.
echo.

echo 13. 测试修改测试用例（用例编码格式错误）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"case_code\": \"invalid-code!\", \"name\": \"编码格式错误用例\", \"description\": \"测试编码格式错误\", \"priority\": \"P2\", \"severity\": \"medium\"}"
echo.
echo.

echo 14. 测试修改测试用例（用例名称过长）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"这是一个非常长的用例名称，超过了系统允许的最大长度限制，应该会触发参数验证错误，用于测试系统对长名称的处理能力\", \"description\": \"测试用例名称过长\", \"priority\": \"P2\", \"severity\": \"medium\"}"
echo.
echo.

echo 15. 测试修改测试用例（用例描述过长）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"描述过长用例\", \"description\": \"这是一个非常长的用例描述，超过了系统允许的最大长度限制，应该会触发参数验证错误，用于测试系统对长描述的处理能力。这个描述包含了大量的文字内容，用于验证系统对描述字段长度的限制功能。\", \"priority\": \"P2\", \"severity\": \"medium\"}"
echo.
echo.

echo 16. 测试修改测试用例（请求体为空）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{}"
echo.
echo.

echo 17. 测试修改测试用例（无认证令牌）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"无认证令牌用例\", \"description\": \"测试无认证令牌\", \"priority\": \"P2\", \"severity\": \"medium\"}"
echo.
echo.

echo 18. 测试修改测试用例（错误的请求方法）
curl -X GET "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"错误请求方法用例\", \"description\": \"测试错误的请求方法\", \"priority\": \"P2\", \"severity\": \"medium\"}"
echo.
echo.

echo 19. 测试修改测试用例（错误的请求路径）
curl -X PUT "http://localhost:8080/testcase/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"错误请求路径用例\", \"description\": \"测试错误的请求路径\", \"priority\": \"P2\", \"severity\": \"medium\"}"
echo.
echo.

echo 20. 测试修改测试用例（更新为模板用例）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"更新为模板用例\", \"description\": \"更新为模板用例\", \"priority\": \"P1\", \"severity\": \"high\", \"is_template\": true, \"is_enabled\": true}"
echo.
echo.

echo 21. 测试修改测试用例（禁用用例）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"禁用用例\", \"description\": \"禁用测试用例\", \"priority\": \"P3\", \"severity\": \"low\", \"is_enabled\": false}"
echo.
echo.

echo 22. 测试修改测试用例（更新版本号）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"更新版本号用例\", \"description\": \"更新版本号\", \"version\": \"2.0\"}"
echo.
echo.

echo 23. 测试修改测试用例（更新JSON字段）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"更新JSON字段用例\", \"tags\": [\"新标签1\", \"新标签2\"], \"pre_conditions\": [{\"type\": \"auth\", \"config\": {\"token\": \"test_token\"}}], \"test_steps\": [{\"step\": 1, \"action\": \"发送请求\", \"expected\": \"成功响应\"}], \"assertions\": [{\"type\": \"status_code\", \"expected\": 200}]}"
echo.
echo.

echo 24. 测试修改测试用例（更新预期响应）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"更新预期响应用例\", \"expected_http_status\": 201, \"expected_response_body\": \"{\\\"code\\\": 1, \\\"msg\\\": \\\"success\\\", \\\"data\\\": {\\\"id\\\": 123}}\", \"expected_response_schema\": {\"type\": \"object\", \"properties\": {\"code\": {\"type\": \"number\"}, \"msg\": {\"type\": \"string\"}, \"data\": {\"type\": \"object\"}}}}"
echo.
echo.

echo 25. 测试修改测试用例（更新提取器和验证器）
curl -X PUT "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"更新提取器和验证器用例\", \"extractors\": [{\"name\": \"user_id\", \"path\": \"$.data.id\", \"type\": \"json_path\"}], \"validators\": [{\"name\": \"response_time\", \"type\": \"response_time\", \"max_time_ms\": 2000}]}"
echo.
echo.

echo 测试完成！
echo.
echo 测试说明：
echo - 用例1001：普通用例，应该更新成功
echo - 用例1002：已删除的用例，应该返回"测试用例已被删除，无法编辑"
echo - 用例1003：权限不足的用例，应该返回"权限不足，无法更新测试用例"
echo - 用例9999：不存在的用例，应该返回"测试用例不存在"
echo - 模板1002：存在的模板用例
echo - 模板9999：不存在的模板用例
echo - 各种参数验证：空值、格式错误、长度超限等
echo - 权限验证：认证和授权检查
echo - 部分更新：只更新提供的字段
echo - 完整更新：更新所有字段
pause