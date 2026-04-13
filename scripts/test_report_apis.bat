@echo off
echo 测试报告管理模块API
echo.

echo 1. 测试获取报告列表（GET /reports）
curl -X GET "http://localhost:8080/api/reports?page=1&page_size=10" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json"
echo.
echo.

echo 2. 测试按项目ID过滤报告列表
curl -X GET "http://localhost:8080/api/reports?project_id=1&report_type=execution&page=1&page_size=20" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json"
echo.
echo.

echo 3. 测试按环境过滤报告列表
curl -X GET "http://localhost:8080/api/reports?environment=test&report_status=completed&success_rate_min=90" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json"
echo.
echo.

echo 4. 测试按时间范围过滤报告列表
curl -X GET "http://localhost:8080/api/reports?start_time_begin=2024-09-01T00:00:00&start_time_end=2024-09-16T23:59:59" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json"
echo.
echo.

echo 5. 测试按标签和排序过滤报告列表
curl -X GET "http://localhost:8080/api/reports?tags=冒烟测试&sort_by=start_time&sort_order=desc" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json"
echo.
echo.

echo 6. 测试根据ID查询报告详情
curl -X GET "http://localhost:8080/api/reports/1001" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json"
echo.
echo.

echo 7. 测试根据项目ID查询报告列表
curl -X GET "http://localhost:8080/api/reports/project/1" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json"
echo.
echo.

echo 8. 测试根据执行ID查询报告
curl -X GET "http://localhost:8080/api/reports/execution/12345" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json"
echo.
echo.

echo 9. 测试创建报告
curl -X POST "http://localhost:8080/api/reports" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json" ^
     -d "{\"reportName\":\"测试报告\",\"reportType\":\"execution\",\"projectId\":1,\"environment\":\"test\",\"generatedBy\":1}"
echo.
echo.

echo 10. 测试更新报告
curl -X PUT "http://localhost:8080/api/reports/1001" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json" ^
     -d "{\"reportName\":\"更新后的测试报告\",\"reportType\":\"execution\",\"projectId\":1,\"environment\":\"test\"}"
echo.
echo.

echo 11. 测试删除报告
curl -X DELETE "http://localhost:8080/api/reports/1001" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json"
echo.
echo.

echo 12. 测试批量删除报告
curl -X DELETE "http://localhost:8080/api/reports/batch" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json" ^
     -d "[1001,1002,1003]"
echo.
echo.

echo 13. 测试更新报告状态
curl -X PATCH "http://localhost:8080/api/reports/1001/status?report_status=completed" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json"
echo.
echo.

echo 14. 测试更新报告文件信息
curl -X PATCH "http://localhost:8080/api/reports/1001/file?file_path=/reports/test.html&file_size=1024000&download_url=/api/reports/1001/download" ^
     -H "Authorization: Bearer your_token_here" ^
     -H "Content-Type: application/json"
echo.
echo.

echo 测试完成！
pause
