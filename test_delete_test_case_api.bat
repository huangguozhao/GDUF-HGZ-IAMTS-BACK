@echo off
echo 测试删除测试用例API
echo.

echo 1. 测试删除测试用例（成功）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 2. 测试删除测试用例（用例不存在）
curl -X DELETE "http://localhost:8080/testcases/9999" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 3. 测试删除测试用例（用例已被删除）
curl -X DELETE "http://localhost:8080/testcases/1002" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 4. 测试删除测试用例（权限不足）
curl -X DELETE "http://localhost:8080/testcases/1003" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 5. 测试删除测试用例（模板用例）
curl -X DELETE "http://localhost:8080/testcases/1004" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 6. 测试删除测试用例（系统用例）
curl -X DELETE "http://localhost:8080/testcases/1005" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 7. 测试删除测试用例（用例正在被使用）
curl -X DELETE "http://localhost:8080/testcases/1006" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 8. 测试删除测试用例（无认证令牌）
curl -X DELETE "http://localhost:8080/testcases/1001"
echo.
echo.

echo 9. 测试删除测试用例（错误的请求方法）
curl -X GET "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 10. 测试删除测试用例（错误的请求路径）
curl -X DELETE "http://localhost:8080/testcase/1001" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 11. 测试删除测试用例（无效的用例ID）
curl -X DELETE "http://localhost:8080/testcases/abc" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 12. 测试删除测试用例（负数用例ID）
curl -X DELETE "http://localhost:8080/testcases/-1" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 13. 测试删除测试用例（零用例ID）
curl -X DELETE "http://localhost:8080/testcases/0" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 14. 测试删除测试用例（大数值用例ID）
curl -X DELETE "http://localhost:8080/testcases/999999999" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 15. 测试删除测试用例（过期令牌）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer expired_token_here"
echo.
echo.

echo 16. 测试删除测试用例（无效令牌）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer invalid_token_here"
echo.
echo.

echo 17. 测试删除测试用例（空令牌）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer "
echo.
echo.

echo 18. 测试删除测试用例（无Bearer前缀）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: your_token_here"
echo.
echo.

echo 19. 测试删除测试用例（错误的Authorization头）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Basic dXNlcjpwYXNz"
echo.
echo.

echo 20. 测试删除测试用例（多个Authorization头）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Authorization: Bearer another_token_here"
echo.
echo.

echo 21. 测试删除测试用例（Content-Type头）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json"
echo.
echo.

echo 22. 测试删除测试用例（Accept头）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Accept: application/json"
echo.
echo.

echo 23. 测试删除测试用例（User-Agent头）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "User-Agent: TestClient/1.0"
echo.
echo.

echo 24. 测试删除测试用例（X-Requested-With头）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "X-Requested-With: XMLHttpRequest"
echo.
echo.

echo 25. 测试删除测试用例（自定义头）
curl -X DELETE "http://localhost:8080/testcases/1001" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "X-Custom-Header: custom_value"
echo.
echo.

echo 26. 测试删除测试用例（并发删除）
curl -X DELETE "http://localhost:8080/testcases/1007" ^
  -H "Authorization: Bearer your_token_here" &
curl -X DELETE "http://localhost:8080/testcases/1007" ^
  -H "Authorization: Bearer your_token_here" &
wait
echo.
echo.

echo 27. 测试删除测试用例（批量删除测试）
for /L %%i in (1008,1,1010) do (
    curl -X DELETE "http://localhost:8080/testcases/%%i" ^
      -H "Authorization: Bearer your_token_here"
    echo.
)
echo.

echo 28. 测试删除测试用例（压力测试）
for /L %%i in (1,1,10) do (
    curl -X DELETE "http://localhost:8080/testcases/1011" ^
      -H "Authorization: Bearer your_token_here"
    echo.
)
echo.

echo 29. 测试删除测试用例（边界值测试）
curl -X DELETE "http://localhost:8080/testcases/1" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X DELETE "http://localhost:8080/testcases/2147483647" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 30. 测试删除测试用例（特殊字符测试）
curl -X DELETE "http://localhost:8080/testcases/1001%20" ^
  -H "Authorization: Bearer your_token_here"
echo.
curl -X DELETE "http://localhost:8080/testcases/1001+" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 测试完成！
echo.
echo 测试说明：
echo - 用例1001：普通用例，应该删除成功
echo - 用例1002：已删除的用例，应该返回"测试用例已被删除"
echo - 用例1003：权限不足的用例，应该返回"权限不足，无法删除测试用例"
echo - 用例1004：模板用例，应该返回"模板用例不能被删除"
echo - 用例1005：系统用例，应该返回"不能删除系统用例"
echo - 用例1006：正在被使用的用例，应该返回"用例正在被测试计划使用，无法删除"
echo - 用例9999：不存在的用例，应该返回"测试用例不存在"
echo - 各种认证和权限测试
echo - 各种边界值和异常情况测试
echo - 并发和压力测试
pause
