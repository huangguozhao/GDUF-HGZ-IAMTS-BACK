@echo off
echo Testing API List Response...
echo.

echo Making API call to get module APIs...
curl -X GET "http://localhost:8080/modules/1/apis?include_statistics=true" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer test-token" ^
  -s

echo.
echo.
echo If you see the response above, check if it contains these new fields:
echo - baseUrl
echo - requestParameters
echo - pathParameters  
echo - requestHeaders
echo - requestBody
echo - authConfig
echo - examples
echo.
pause
