@echo off
echo 测试删除接口API
echo.

echo 1. 测试删除接口（成功）
curl -X DELETE "http://localhost:8080/apis/1001" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 2. 测试删除接口（接口不存在）
curl -X DELETE "http://localhost:8080/apis/9999" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 3. 测试删除接口（接口已被删除）
curl -X DELETE "http://localhost:8080/apis/1002" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 4. 测试删除接口（权限不足）
curl -X DELETE "http://localhost:8080/apis/1003" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 5. 测试删除接口（系统接口）
curl -X DELETE "http://localhost:8080/apis/1004" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 6. 测试删除接口（存在测试用例）
curl -X DELETE "http://localhost:8080/apis/1005" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 7. 测试删除接口（存在前置条件）
curl -X DELETE "http://localhost:8080/apis/1006" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 8. 测试删除接口（正在被使用）
curl -X DELETE "http://localhost:8080/apis/1007" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 9. 测试删除接口（接口ID为空）
curl -X DELETE "http://localhost:8080/apis/" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 10. 测试删除接口（无效的接口ID格式）
curl -X DELETE "http://localhost:8080/apis/invalid" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 11. 测试删除接口（无认证令牌）
curl -X DELETE "http://localhost:8080/apis/1001"
echo.
echo.

echo 12. 测试删除接口（无效的认证令牌）
curl -X DELETE "http://localhost:8080/apis/1001" ^
  -H "Authorization: Bearer invalid_token"
echo.
echo.

echo 13. 测试删除接口（过期认证令牌）
curl -X DELETE "http://localhost:8080/apis/1001" ^
  -H "Authorization: Bearer expired_token"
echo.
echo.

echo 14. 测试删除接口（错误的请求方法）
curl -X POST "http://localhost:8080/apis/1001" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 15. 测试删除接口（错误的请求路径）
curl -X DELETE "http://localhost:8080/api/1001" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 测试完成！
echo.
echo 测试说明：
echo - 接口1001：普通接口，应该删除成功
echo - 接口1002：已删除的接口，应该返回"接口已被删除"
echo - 接口1003：权限不足的接口，应该返回"权限不足，无法删除接口"
echo - 接口1004：系统接口（编码以SYS_开头或名称包含"系统"），应该返回"不能删除系统接口"
echo - 接口1005：存在测试用例的接口，应该返回"接口存在测试用例，无法删除"
echo - 接口1006：存在前置条件的接口，应该返回"接口存在前置条件配置，无法删除"
echo - 接口1007：正在被使用的接口，应该返回"接口正在被测试计划使用，无法删除"
echo - 接口9999：不存在的接口，应该返回"接口不存在"
echo - 各种参数验证：空值、格式错误等
echo - 权限验证：认证和授权检查
pause
