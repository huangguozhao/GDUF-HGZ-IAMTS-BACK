@echo off
echo 测试删除模块接口
echo.

echo 1. 测试删除普通模块（成功）
curl -X DELETE "http://localhost:8080/modules/1" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 2. 测试删除不存在的模块
curl -X DELETE "http://localhost:8080/modules/999" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 3. 测试删除已删除的模块
curl -X DELETE "http://localhost:8080/modules/2" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 4. 测试删除系统模块（应该失败）
curl -X DELETE "http://localhost:8080/modules/3" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 5. 测试删除有子模块的模块（应该失败）
curl -X DELETE "http://localhost:8080/modules/4" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 6. 测试删除有接口数据的模块（应该失败）
curl -X DELETE "http://localhost:8080/modules/5" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 7. 测试删除正在被使用的模块（应该失败）
curl -X DELETE "http://localhost:8080/modules/6" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 8. 测试删除模块（权限不足）
curl -X DELETE "http://localhost:8080/modules/7" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 9. 测试删除模块（无认证令牌）
curl -X DELETE "http://localhost:8080/modules/1"
echo.
echo.

echo 10. 测试删除模块（无效的模块ID格式）
curl -X DELETE "http://localhost:8080/modules/invalid" ^
  -H "Authorization: Bearer your_token_here"
echo.
echo.

echo 测试完成！
echo.
echo 测试说明：
echo - 模块1：普通模块，应该删除成功
echo - 模块2：已删除的模块，应该返回"模块已被删除"
echo - 模块3：系统模块（编码以SYS_开头或名称包含"系统"），应该返回"不能删除系统模块"
echo - 模块4：有子模块的模块，应该返回"模块存在子模块，无法删除"
echo - 模块5：有接口数据的模块，应该返回"模块存在接口数据，无法删除"
echo - 模块6：正在被使用的模块，应该返回"模块正在被使用，无法删除"
echo - 模块7：非创建者删除，应该返回"权限不足，无法删除模块"
echo - 模块999：不存在的模块，应该返回"模块不存在"
pause
