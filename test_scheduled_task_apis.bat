@echo off
chcp 65001
echo ============================================
echo  定时测试任务 API 测试脚本
echo ============================================
echo.

set BASE_URL=http://localhost:8080/api

:: 设置Token（请替换为有效的JWT Token）
set TOKEN=your_jwt_token_here

echo 1. 创建定时任务（每日执行）
echo ----------------------------------------------------
curl -X POST "%BASE_URL%/scheduled-tasks" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{
    \"taskName\": \"每日API测试\",
    \"description\": \"每天早上9点执行用户管理模块的所有测试\",
    \"taskType\": \"module\",
    \"targetId\": 1,
    \"triggerType\": \"daily\",
    \"dailyHour\": 9,
    \"dailyMinute\": 0,
    \"executionEnvironment\": \"test\",
    \"concurrency\": 3,
    \"executionStrategy\": \"by_module\",
    \"retryEnabled\": true,
    \"maxRetryAttempts\": 2,
    \"notifyOnFailure\": true,
    \"notificationRecipients\": \"test@example.com\"
  }"
echo.
echo.

echo 2. 创建定时任务（使用Cron表达式）
echo ----------------------------------------------------
curl -X POST "%BASE_URL%/scheduled-tasks" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{
    \"taskName\": \"每周一API测试\",
    \"description\": \"每周一早上8点执行所有API测试\",
    \"taskType\": \"api\",
    \"targetId\": 1,
    \"triggerType\": \"cron\",
    \"cronExpression\": \"0 0 8 ? * MON\",
    \"executionEnvironment\": \"test\",
    \"notifyOnFailure\": true
  }"
echo.
echo.

echo 3. 查询任务列表
echo ----------------------------------------------------
curl -X GET "%BASE_URL%/scheduled-tasks?page=1&pageSize=10" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 4. 获取任务详情（假设taskId=1）
echo ----------------------------------------------------
curl -X GET "%BASE_URL%/scheduled-tasks/1" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 5. 更新任务（假设taskId=1）
echo ----------------------------------------------------
curl -X PUT "%BASE_URL%/scheduled-tasks/1" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{
    \"taskName\": \"每日API测试（更新）\",
    \"description\": \"更新后的描述\",
    \"triggerType\": \"cron\",
    \"cronExpression\": \"0 0 10 * * ?\"
  }"
echo.
echo.

echo 6. 启用任务（假设taskId=1）
echo ----------------------------------------------------
curl -X POST "%BASE_URL%/scheduled-tasks/1/enable" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 7. 禁用任务（假设taskId=1）
echo ----------------------------------------------------
curl -X POST "%BASE_URL%/scheduled-tasks/1/disable" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 8. 立即执行任务（假设taskId=1）
echo ----------------------------------------------------
curl -X POST "%BASE_URL%/scheduled-tasks/1/execute" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 9. 获取执行历史（假设taskId=1）
echo ----------------------------------------------------
curl -X GET "%BASE_URL%/scheduled-tasks/1/history?page=1&pageSize=10" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 10. 获取执行统计（假设taskId=1）
echo ----------------------------------------------------
curl -X GET "%BASE_URL%/scheduled-tasks/1/statistics" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 11. 获取执行记录详情（假设executionId=1）
echo ----------------------------------------------------
curl -X GET "%BASE_URL%/scheduled-tasks/executions/1" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo 12. 删除任务（假设taskId=1）
echo ----------------------------------------------------
curl -X DELETE "%BASE_URL%/scheduled-tasks/1" ^
  -H "Authorization: Bearer %TOKEN%"
echo.
echo.

echo ============================================
echo  测试完成！
echo ============================================
pause

