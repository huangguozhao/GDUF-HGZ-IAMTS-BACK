@echo off
SETLOCAL

REM Configuration
SET BASE_URL=http://localhost:8080/api
SET AUTH_TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY3ODkwNTYwMCwiZXhwIjoxNjc4OTEyODAwfQ.your_jwt_token_here

echo.
echo =====================================
echo Testing Get Recent Projects API (GET /recent-projects)
echo =====================================
echo.

REM Test Case 1: Get recent projects with default parameters
echo --- Test Case 1: Get recent projects with default parameters ---
curl -X GET "%BASE_URL%/recent-projects" ^
     -H "Authorization: Bearer %AUTH_TOKEN%"
echo.
echo.

REM Test Case 2: Get recent projects with custom time range and pagination
echo --- Test Case 2: Get recent projects with time_range=30d, page=1, page_size=15 ---
curl -X GET "%BASE_URL%/recent-projects?time_range=30d&page=1&page_size=15" ^
     -H "Authorization: Bearer %AUTH_TOKEN%"
echo.
echo.

REM Test Case 3: Get recent projects with include_stats=true
echo --- Test Case 3: Get recent projects with include_stats=true ---
curl -X GET "%BASE_URL%/recent-projects?include_stats=true" ^
     -H "Authorization: Bearer %AUTH_TOKEN%"
echo.
echo.

REM Test Case 4: Get recent projects with custom sorting
echo --- Test Case 4: Get recent projects with sort_by=updated_at&sort_order=desc ---
curl -X GET "%BASE_URL%/recent-projects?sort_by=updated_at&sort_order=desc" ^
     -H "Authorization: Bearer %AUTH_TOKEN%"
echo.
echo.

REM Test Case 5: Get recent projects with time_range=1d
echo --- Test Case 5: Get recent projects with time_range=1d ---
curl -X GET "%BASE_URL%/recent-projects?time_range=1d" ^
     -H "Authorization: Bearer %AUTH_TOKEN%"
echo.
echo.

REM Test Case 6: Invalid time_range (expecting 400)
echo --- Test Case 6: Invalid time_range=5d (expecting 400) ---
curl -X GET "%BASE_URL%/recent-projects?time_range=5d" ^
     -H "Authorization: Bearer %AUTH_TOKEN%"
echo.
echo.

REM Test Case 7: Invalid page_size (expecting 400)
echo --- Test Case 7: Invalid page_size=25 (expecting 400) ---
curl -X GET "%BASE_URL%/recent-projects?page_size=25" ^
     -H "Authorization: Bearer %AUTH_TOKEN%"
echo.
echo.

REM Test Case 8: Invalid sort_by field (expecting 400)
echo --- Test Case 8: Invalid sort_by=invalid_field (expecting 400) ---
curl -X GET "%BASE_URL%/recent-projects?sort_by=invalid_field" ^
     -H "Authorization: Bearer %AUTH_TOKEN%"
echo.
echo.

REM Test Case 9: Get recent projects with sort_by=created_at&sort_order=asc
echo --- Test Case 9: Get recent projects with sort_by=created_at&sort_order=asc ---
curl -X GET "%BASE_URL%/recent-projects?sort_by=created_at&sort_order=asc" ^
     -H "Authorization: Bearer %AUTH_TOKEN%"
echo.
echo.

REM Test Case 10: Get recent projects with all parameters
echo --- Test Case 10: Get recent projects with all parameters ---
curl -X GET "%BASE_URL%/recent-projects?time_range=7d&include_stats=true&sort_by=last_accessed&sort_order=desc&page=1&page_size=10" ^
     -H "Authorization: Bearer %AUTH_TOKEN%"
echo.
echo.

ENDLOCAL




