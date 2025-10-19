@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

REM TestExecutionController API Test Script for Windows

set BASE_URL=http://localhost:8080/api
set TOTAL=0
set SUCCESS=0
set FAILED=0

echo ======================================
echo TestExecutionController API Testing
echo ======================================
echo.

REM Test 1
set /a TOTAL+=1
echo [Test !TOTAL!] Execute single test case
curl -s -o nul -w "HTTP %%{http_code}\n" -X POST "%BASE_URL%/test-cases/1/execute" -H "Content-Type: application/json" -d "{\"environment\":\"test\",\"timeout\":30000}"
echo.

REM Test 2
set /a TOTAL+=1
echo [Test !TOTAL!] Execute test case async
curl -s -o nul -w "HTTP %%{http_code}\n" -X POST "%BASE_URL%/test-cases/1/execute-async" -H "Content-Type: application/json" -d "{\"environment\":\"test\",\"async\":true}"
echo.

REM Test 3
set /a TOTAL+=1
echo [Test !TOTAL!] Get task status
curl -s -o nul -w "HTTP %%{http_code}\n" -X GET "%BASE_URL%/tasks/test-task-id-123/status"
echo.

REM Test 4
set /a TOTAL+=1
echo [Test !TOTAL!] Cancel task
curl -s -o nul -w "HTTP %%{http_code}\n" -X POST "%BASE_URL%/tasks/test-task-id-123/cancel" -H "Content-Type: application/json" -d "{}"
echo.

REM Test 5
set /a TOTAL+=1
echo [Test !TOTAL!] Get execution result
curl -s -o nul -w "HTTP %%{http_code}\n" -X GET "%BASE_URL%/test-results/1"
echo.

REM Test 6
set /a TOTAL+=1
echo [Test !TOTAL!] Get execution logs
curl -s -o nul -w "HTTP %%{http_code}\n" -X GET "%BASE_URL%/test-results/1/logs"
echo.

REM Test 7
set /a TOTAL+=1
echo [Test !TOTAL!] Generate test report
curl -s -o nul -w "HTTP %%{http_code}\n" -X POST "%BASE_URL%/test-results/1/report" -H "Content-Type: application/json" -d "{}"
echo.

REM Module Tests
set /a TOTAL+=1
echo [Test !TOTAL!] Execute module test
curl -s -o nul -w "HTTP %%{http_code}\n" -X POST "%BASE_URL%/modules/1/execute" -H "Content-Type: application/json" -d "{\"environment\":\"test\",\"concurrency\":5}"
echo.

REM Project Tests
set /a TOTAL+=1
echo [Test !TOTAL!] Execute project test
curl -s -o nul -w "HTTP %%{http_code}\n" -X POST "%BASE_URL%/projects/1/execute" -H "Content-Type: application/json" -d "{\"environment\":\"test\",\"concurrency\":10}"
echo.

REM API Tests
set /a TOTAL+=1
echo [Test !TOTAL!] Execute API test
curl -s -o nul -w "HTTP %%{http_code}\n" -X POST "%BASE_URL%/apis/1/execute" -H "Content-Type: application/json" -d "{\"environment\":\"test\",\"concurrency\":3}"
echo.

REM Test Suite Tests
set /a TOTAL+=1
echo [Test !TOTAL!] Execute test suite
curl -s -o nul -w "HTTP %%{http_code}\n" -X POST "%BASE_URL%/test-suites/1/execute" -H "Content-Type: application/json" -d "{\"environment\":\"test\",\"concurrency\":5}"
echo.

REM Test Results Query
set /a TOTAL+=1
echo [Test !TOTAL!] Get test results list (no params)
curl -s -w "\n" -X GET "%BASE_URL%/test-results"
echo.

set /a TOTAL+=1
echo [Test !TOTAL!] Get test results list (with pagination)
curl -s -w "\n" -X GET "%BASE_URL%/test-results?page=1&page_size=10"
echo.

set /a TOTAL+=1
echo [Test !TOTAL!] Query failed test results
curl -s -w "\n" -X GET "%BASE_URL%/test-results?status=failed&page=1&page_size=20"
echo.

set /a TOTAL+=1
echo [Test !TOTAL!] Query by task type
curl -s -w "\n" -X GET "%BASE_URL%/test-results?task_type=test_case&ref_id=101"
echo.

echo.
echo ======================================
echo Test completed! Total: !TOTAL! APIs
echo ======================================
pause

