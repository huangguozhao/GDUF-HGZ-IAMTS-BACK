@echo off
echo 测试添加测试用例API
echo.

echo 1. 测试添加测试用例（成功）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"用户登录-成功场景\", \"description\": \"测试用户使用正确凭据登录的成功情况\", \"priority\": \"P0\", \"severity\": \"high\", \"tags\": [\"冒烟测试\", \"登录功能\"], \"is_enabled\": true}"
echo.
echo.

echo 2. 测试添加测试用例（自动生成用例编码）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"用户登录-失败场景\", \"description\": \"测试用户使用错误凭据登录的失败情况\", \"priority\": \"P1\", \"severity\": \"medium\", \"tags\": [\"登录功能\", \"异常测试\"], \"is_enabled\": true}"
echo.
echo.

echo 3. 测试添加测试用例（指定用例编码）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"case_code\": \"TC-API-101-CUSTOM-001\", \"name\": \"用户登录-自定义编码\", \"description\": \"测试自定义用例编码\", \"priority\": \"P2\", \"severity\": \"low\", \"is_enabled\": true}"
echo.
echo.

echo 4. 测试添加测试用例（基于模板）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"用户登录-基于模板\", \"description\": \"基于模板创建的测试用例\", \"template_id\": 1001, \"priority\": \"P1\", \"severity\": \"high\", \"is_enabled\": true}"
echo.
echo.

echo 5. 测试添加测试用例（完整配置）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"用户登录-完整配置\", \"description\": \"包含完整配置的测试用例\", \"priority\": \"P0\", \"severity\": \"critical\", \"tags\": [\"冒烟测试\", \"登录功能\", \"核心功能\"], \"request_override\": {\"request_body\": {\"username\": \"testuser\", \"password\": \"Password123!\"}}, \"expected_http_status\": 200, \"expected_response_schema\": {\"type\": \"object\", \"properties\": {\"code\": {\"type\": \"number\"}, \"msg\": {\"type\": \"string\"}, \"data\": {\"type\": \"object\", \"properties\": {\"token\": {\"type\": \"string\"}}}}}, \"assertions\": [{\"type\": \"status_code\", \"expected\": 200}, {\"type\": \"json_path\", \"expression\": \"$.code\", \"expected\": 1}], \"is_enabled\": true}"
echo.
echo.

echo 6. 测试添加测试用例（接口不存在）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 9999, \"name\": \"不存在的接口用例\", \"description\": \"测试不存在的接口\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 7. 测试添加测试用例（接口已禁用）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 1002, \"name\": \"已禁用接口用例\", \"description\": \"测试已禁用的接口\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 8. 测试添加测试用例（用例编码已存在）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"case_code\": \"TC-API-101-001\", \"name\": \"重复编码用例\", \"description\": \"测试重复的用例编码\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 9. 测试添加测试用例（模板用例不存在）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"模板不存在用例\", \"description\": \"测试不存在的模板\", \"template_id\": 9999, \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 10. 测试添加测试用例（权限不足）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 1003, \"name\": \"权限不足用例\", \"description\": \"测试权限不足的情况\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 11. 测试添加测试用例（用例名称为空）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"\", \"description\": \"测试用例名称为空\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 12. 测试添加测试用例（接口ID为空）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"接口ID为空用例\", \"description\": \"测试接口ID为空\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 13. 测试添加测试用例（无效的优先级）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"无效优先级用例\", \"description\": \"测试无效的优先级\", \"priority\": \"P5\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 14. 测试添加测试用例（无效的严重程度）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"无效严重程度用例\", \"description\": \"测试无效的严重程度\", \"priority\": \"P2\", \"severity\": \"invalid\", \"is_enabled\": true}"
echo.
echo.

echo 15. 测试添加测试用例（用例编码格式错误）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"case_code\": \"invalid-code!\", \"name\": \"编码格式错误用例\", \"description\": \"测试编码格式错误\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 16. 测试添加测试用例（用例名称过长）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"这是一个非常长的用例名称，超过了系统允许的最大长度限制，应该会触发参数验证错误，用于测试系统对长名称的处理能力\", \"description\": \"测试用例名称过长\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 17. 测试添加测试用例（用例描述过长）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"描述过长用例\", \"description\": \"这是一个非常长的用例描述，超过了系统允许的最大长度限制，应该会触发参数验证错误，用于测试系统对长描述的处理能力。这个描述包含了大量的文字内容，用于验证系统对描述字段长度的限制功能。\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 18. 测试添加测试用例（请求体为空）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{}"
echo.
echo.

echo 19. 测试添加测试用例（无认证令牌）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"无认证令牌用例\", \"description\": \"测试无认证令牌\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 20. 测试添加测试用例（错误的请求方法）
curl -X GET "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"错误请求方法用例\", \"description\": \"测试错误的请求方法\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 21. 测试添加测试用例（错误的请求路径）
curl -X POST "http://localhost:8080/testcase" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"错误请求路径用例\", \"description\": \"测试错误的请求路径\", \"priority\": \"P2\", \"severity\": \"medium\", \"is_enabled\": true}"
echo.
echo.

echo 22. 测试添加测试用例（模板用例）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"模板用例\", \"description\": \"创建一个模板用例\", \"priority\": \"P1\", \"severity\": \"high\", \"is_template\": true, \"is_enabled\": true}"
echo.
echo.

echo 23. 测试添加测试用例（禁用用例）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"禁用用例\", \"description\": \"创建一个禁用的测试用例\", \"priority\": \"P3\", \"severity\": \"low\", \"is_enabled\": false}"
echo.
echo.

echo 24. 测试添加测试用例（最小配置）
curl -X POST "http://localhost:8080/testcases" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"api_id\": 101, \"name\": \"最小配置用例\"}"
echo.
echo.

echo 测试完成！
echo.
echo 测试说明：
echo - 接口101：普通接口，应该创建成功
echo - 接口1002：已禁用的接口，应该返回"接口已禁用，无法创建用例"
echo - 接口1003：权限不足的接口，应该返回"权限不足，无法创建测试用例"
echo - 接口9999：不存在的接口，应该返回"接口不存在"
echo - 模板1001：存在的模板用例
echo - 模板9999：不存在的模板用例
echo - 各种参数验证：空值、格式错误、长度超限等
echo - 权限验证：认证和授权检查
pause
