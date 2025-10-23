@echo off
echo Testing API List Enhancement...
echo.

echo Testing GET /modules/1/apis with enhanced fields...
curl -X GET "http://localhost:8080/modules/1/apis?include_statistics=true&page=1&page_size=5" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your-token-here" ^
  -w "\n\nHTTP Status: %%{http_code}\nResponse Time: %%{time_total}s\n" ^
  -s

echo.
echo Testing with specific filters...
curl -X GET "http://localhost:8080/modules/1/apis?method=POST&status=active&include_statistics=true" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer your-token-here" ^
  -w "\n\nHTTP Status: %%{http_code}\nResponse Time: %%{time_total}s\n" ^
  -s

echo.
echo Testing completed.
pause
