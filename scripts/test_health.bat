@echo off
chcp 65001
echo ============================================
echo Health Check Test
echo ============================================
echo.

echo Test 1: Simple health check
echo URL: http://localhost:8080/api/health
echo.
curl -X GET "http://localhost:8080/api/health"
echo.
echo.

echo Test 2: Detailed health check
echo URL: http://localhost:8080/api/health/detail
echo.
curl -X GET "http://localhost:8080/api/health/detail"
echo.
echo.

echo Test 3: JSON test
echo URL: http://localhost:8080/api/test/json
echo.
curl -X GET "http://localhost:8080/api/test/json"
echo.
echo.

echo ============================================
echo If all 3 tests return success, the basic
echo system is working. The problem is in the
echo TestExecutionController implementation.
echo ============================================
pause

