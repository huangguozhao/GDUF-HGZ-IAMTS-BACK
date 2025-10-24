@echo off
chcp 65001 >nul
echo ============================================
echo 测试项目统计数据接口
echo ============================================
echo.

REM 设置变量
set BASE_URL=http://localhost:8080/api
set TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJpYXQiOjE3Mjk2NTU5MzUsImV4cCI6MTczMDI2MDczNX0.YXgvG0nXEo-Bs8NaPuIvbbAH_u5S7BImVzDSjIFbqhQ

echo.
echo [测试1] 获取项目ID=1的统计数据
echo ----------------------------------------
curl -X GET "%BASE_URL%/projects/1/statistics" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -s | jq .

echo.
echo.
echo ============================================
echo 测试完成
echo ============================================
pause

