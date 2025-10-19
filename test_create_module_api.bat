@echo off
echo 测试创建模块接口
echo.

echo 1. 测试创建根模块（支付管理模块）
curl -X POST "http://localhost:8080/modules" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\":\"PAYMENT_MGMT\",\"project_id\":1,\"name\":\"支付管理模块\",\"description\":\"支付相关的功能模块，包括支付、退款、对账等功能\",\"sort_order\":3,\"status\":\"active\",\"owner_id\":456,\"tags\":[\"核心模块\",\"支付相关\",\"财务\"]}"
echo.
echo.

echo 2. 测试创建子模块（认证子模块）
curl -X POST "http://localhost:8080/modules" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\":\"AUTH_SUB\",\"project_id\":1,\"parent_module_id\":1,\"name\":\"认证子模块\",\"description\":\"用户认证和授权子模块\",\"sort_order\":1,\"status\":\"active\",\"owner_id\":123,\"tags\":[\"认证\",\"安全\"]}"
echo.
echo.

echo 3. 测试创建模块（最小参数）
curl -X POST "http://localhost:8080/modules" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\":\"MINIMAL_MODULE\",\"project_id\":1,\"name\":\"最小参数模块\"}"
echo.
echo.

echo 4. 测试创建模块（模块编码已存在）
curl -X POST "http://localhost:8080/modules" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\":\"PAYMENT_MGMT\",\"project_id\":1,\"name\":\"重复编码模块\"}"
echo.
echo.

echo 5. 测试创建模块（项目不存在）
curl -X POST "http://localhost:8080/modules" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\":\"INVALID_PROJECT\",\"project_id\":999,\"name\":\"无效项目模块\"}"
echo.
echo.

echo 6. 测试创建模块（父模块不存在）
curl -X POST "http://localhost:8080/modules" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\":\"INVALID_PARENT\",\"project_id\":1,\"parent_module_id\":999,\"name\":\"无效父模块\"}"
echo.
echo.

echo 7. 测试创建模块（负责人不存在）
curl -X POST "http://localhost:8080/modules" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\":\"INVALID_OWNER\",\"project_id\":1,\"name\":\"无效负责人模块\",\"owner_id\":999}"
echo.
echo.

echo 8. 测试创建模块（模块编码格式错误）
curl -X POST "http://localhost:8080/modules" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\":\"invalid-code\",\"project_id\":1,\"name\":\"格式错误模块\"}"
echo.
echo.

echo 9. 测试创建模块（缺少必需参数）
curl -X POST "http://localhost:8080/modules" ^
  -H "Authorization: Bearer your_token_here" ^
  -H "Content-Type: application/json" ^
  -d "{\"project_id\":1,\"name\":\"缺少编码模块\"}"
echo.
echo.

echo 10. 测试创建模块（无认证令牌）
curl -X POST "http://localhost:8080/modules" ^
  -H "Content-Type: application/json" ^
  -d "{\"module_code\":\"NO_AUTH\",\"project_id\":1,\"name\":\"无认证模块\"}"
echo.
echo.

echo 测试完成！
pause
