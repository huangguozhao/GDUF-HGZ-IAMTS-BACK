@echo off
echo Testing Enhanced API Response...
echo.

echo 1. Testing basic API list without filters...
curl -X GET "http://localhost:8080/modules/1/apis" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer test-token" ^
  -w "\n\nHTTP Status: %%{http_code}\n" ^
  -s | jq .

echo.
echo 2. Testing API list with statistics...
curl -X GET "http://localhost:8080/modules/1/apis?include_statistics=true" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer test-token" ^
  -w "\n\nHTTP Status: %%{http_code}\n" ^
  -s | jq .

echo.
echo 3. Testing API list with specific method filter...
curl -X GET "http://localhost:8080/modules/1/apis?method=POST&include_statistics=true" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer test-token" ^
  -w "\n\nHTTP Status: %%{http_code}\n" ^
  -s | jq .

echo.
echo 4. Testing API list with pagination...
curl -X GET "http://localhost:8080/modules/1/apis?page=1&page_size=2&include_statistics=true" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer test-token" ^
  -w "\n\nHTTP Status: %%{http_code}\n" ^
  -s | jq .

echo.
echo Testing completed. Check if the response includes the new fields:
echo - baseUrl
echo - requestParameters  
echo - pathParameters
echo - requestHeaders
echo - requestBody
echo - authConfig
echo - examples
echo.
pause
