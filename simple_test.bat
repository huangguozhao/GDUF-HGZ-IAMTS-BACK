@echo off
chcp 65001
echo ============================================
echo Simple API Test - Show Full Response
echo ============================================
echo.

echo Test 1: Get test results list (simplest API)
echo URL: http://localhost:8080/api/test-results
echo.
curl -X GET "http://localhost:8080/api/test-results"
echo.
echo.

echo Test 2: Get test results with pagination
echo URL: http://localhost:8080/api/test-results?page=1^&page_size=10
echo.
curl -X GET "http://localhost:8080/api/test-results?page=1&page_size=10"
echo.
echo.

echo Test 3: Get test result detail
echo URL: http://localhost:8080/api/test-results/1
echo.
curl -X GET "http://localhost:8080/api/test-results/1"
echo.
echo.

echo Test 4: Get test result detail with options
echo URL: http://localhost:8080/api/test-results/1?include_artifacts=true
echo.
curl -X GET "http://localhost:8080/api/test-results/1?include_artifacts=true"
echo.
echo.

echo Test 5: Get test statistics (default 7 days)
echo URL: http://localhost:8080/api/test-results/statistics
echo.
curl -X GET "http://localhost:8080/api/test-results/statistics"
echo.
echo.

echo Test 6: Get test statistics with grouping
echo URL: http://localhost:8080/api/test-results/statistics?group_by=priority
echo.
curl -X GET "http://localhost:8080/api/test-results/statistics?group_by=priority"
echo.
echo.

echo Test 7: Get test statistics with comparison
echo URL: http://localhost:8080/api/test-results/statistics?include_comparison=true
echo.
curl -X GET "http://localhost:8080/api/test-results/statistics?include_comparison=true"
echo.
echo.

echo Test 8: Get weekly execution (default)
echo URL: http://localhost:8080/api/weekly-execution
echo.
curl -X GET "http://localhost:8080/api/weekly-execution"
echo.
echo.

echo Test 9: Get weekly execution with performance
echo URL: http://localhost:8080/api/weekly-execution?include_performance=true
echo.
curl -X GET "http://localhost:8080/api/weekly-execution?include_performance=true"
echo.
echo.

echo Test 10: Get dashboard summary (default)
echo URL: http://localhost:8080/api/dashboard/summary
echo.
curl -X GET "http://localhost:8080/api/dashboard/summary"
echo.
echo.

echo Test 11: Get dashboard summary with 30 days
echo URL: http://localhost:8080/api/dashboard/summary?time_range=30d
echo.
curl -X GET "http://localhost:8080/api/dashboard/summary?time_range=30d"
echo.
echo.

echo ============================================
echo All 5 test result APIs tested!
echo 1. List API (pagination)
echo 2. Detail API (single result)
echo 3. Statistics API (aggregation)
echo 4. Weekly Execution API (dashboard)
echo 5. Dashboard Summary API (personal)
echo ============================================
pause

