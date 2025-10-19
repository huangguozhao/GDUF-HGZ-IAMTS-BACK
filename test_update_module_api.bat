@echo off
echo 测试修改模块信息接口
echo.

echo 1. 测试修改模块名称（成功）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"支付管理模块-新版\"}"
echo.
echo.

echo 2. 测试修改模块描述（成功）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"description\": \"更新后的支付模块描述，包含新功能\"}"
echo.
echo.

echo 3. 测试修改模块状态（成功）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"status\": \"active\"}"
echo.
echo.

echo 4. 测试修改模块负责人（成功）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"owner_id\": 789}"
echo.
echo.

echo 5. 测试修改模块标签（成功）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"tags\": [\"核心模块\", \"支付\", \"财务\", \"重要\"]}"
echo.
echo.

echo 6. 测试修改模块编码（成功）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\": \"PAYMENT_NEW\"}"
echo.
echo.

echo 7. 测试修改父模块（成功）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"parent_module_id\": 5}"
echo.
echo.

echo 8. 测试修改排序顺序（成功）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"sort_order\": 2}"
echo.
echo.

echo 9. 测试修改不存在的模块
curl -X PUT "http://localhost:8080/modules/999" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"测试模块\"}"
echo.
echo.

echo 10. 测试修改已删除的模块
curl -X PUT "http://localhost:8080/modules/2" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"测试模块\"}"
echo.
echo.

echo 11. 测试修改系统模块（应该失败）
curl -X PUT "http://localhost:8080/modules/3" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"测试模块\"}"
echo.
echo.

echo 12. 测试修改模块编码为已存在的编码（应该失败）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\": \"EXISTING_CODE\"}"
echo.
echo.

echo 13. 测试修改父模块为不存在的模块（应该失败）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"parent_module_id\": 999}"
echo.
echo.

echo 14. 测试修改父模块为自己（应该失败）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"parent_module_id\": 1}"
echo.
echo.

echo 15. 测试修改负责人为不存在的用户（应该失败）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"owner_id\": 999}"
echo.
echo.

echo 16. 测试修改模块状态为无效状态（应该失败）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"status\": \"invalid\"}"
echo.
echo.

echo 17. 测试修改模块（权限不足）
curl -X PUT "http://localhost:8080/modules/7" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"测试模块\"}"
echo.
echo.

echo 18. 测试修改模块（无认证令牌）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"测试模块\"}"
echo.
echo.

echo 19. 测试修改模块（无效的模块ID格式）
curl -X PUT "http://localhost:8080/modules/invalid" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"测试模块\"}"
echo.
echo.

echo 20. 测试修改模块（空的请求体）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{}"
echo.
echo.

echo 21. 测试修改模块（空的模块名称）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"\"}"
echo.
echo.

echo 22. 测试修改模块（模块名称过长）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"这是一个非常长的模块名称，超过了255个字符的限制，应该会返回错误信息，因为模块名称长度不能超过255个字符，这是一个测试用例，用来验证系统对输入长度的限制功能是否正常工作，确保数据完整性和系统稳定性\"}"
echo.
echo.

echo 23. 测试修改模块（模块编码格式错误）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\": \"invalid-code-with-lowercase\"}"
echo.
echo.

echo 24. 测试修改模块（循环引用）
curl -X PUT "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"parent_module_id\": 4}"
echo.
echo.

echo 测试完成！
echo.
echo 测试说明：
echo - 模块1：普通模块，应该修改成功
echo - 模块2：已删除的模块，应该返回"模块已被删除，无法编辑"
echo - 模块3：系统模块（编码以SYS_开头或名称包含"系统"），应该返回"不能修改系统模块"
echo - 模块7：非创建者修改，应该返回"权限不足，无法编辑模块信息"
echo - 模块999：不存在的模块，应该返回"模块不存在"
echo - 各种参数验证：空值、格式错误、长度超限等
echo - 业务逻辑验证：编码重复、父模块不存在、循环引用等
pause
