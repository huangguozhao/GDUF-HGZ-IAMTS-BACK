# Allure风格测试报告 - API使用示例

## 📋 API概述

**端点**: `GET /api/reports/{reportId}/export/allure`  
**认证**: 需要登录（Bearer Token）  
**响应类型**: `text/html;charset=UTF-8`  
**文件格式**: HTML

## 🔧 请求参数

### 路径参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| reportId | Long | 是 | 测试报告ID |

### 查询参数
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| locale | String | 否 | zh_CN | 语言环境：zh_CN（中文）、en_US（英文） |

## 📤 响应说明

### 成功响应 (200 OK)
```
Content-Type: text/html;charset=UTF-8
Content-Disposition: attachment; filename="Allure测试报告_20241026_143052.html"
Cache-Control: no-cache, no-store, must-revalidate
Pragma: no-cache
Expires: 0

<!DOCTYPE html>
<html lang="zh-CN">
...
</html>
```

### 错误响应

#### 400 Bad Request
```json
{
  "code": -3,
  "msg": "报告ID不能为空"
}
```

#### 404 Not Found
```json
{
  "code": -4,
  "msg": "报告不存在"
}
```

#### 500 Internal Server Error
```json
{
  "code": -5,
  "msg": "导出报告失败：数据库连接异常"
}
```

## 💻 使用示例

### 1. cURL (Windows)

#### 基本用法
```bash
curl -X GET "http://localhost:8080/api/reports/196/export/allure" ^
     -o "Allure测试报告.html"
```

#### 指定语言（中文）
```bash
curl -X GET "http://localhost:8080/api/reports/196/export/allure?locale=zh_CN" ^
     -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
     -o "Allure测试报告_中文.html"
```

#### 指定语言（英文）
```bash
curl -X GET "http://localhost:8080/api/reports/196/export/allure?locale=en_US" ^
     -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
     -o "Allure_Test_Report_EN.html"
```

#### 显示详细信息
```bash
curl -X GET "http://localhost:8080/api/reports/196/export/allure?locale=zh_CN" ^
     -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
     -o "report.html" ^
     -w "\n状态码: %%{http_code}\n文件大小: %%{size_download} bytes\n下载时间: %%{time_total}s\n"
```

### 2. cURL (Linux/Mac)

```bash
# 基本用法
curl -X GET "http://localhost:8080/api/reports/196/export/allure" \
     -o "Allure测试报告.html"

# 带认证
curl -X GET "http://localhost:8080/api/reports/196/export/allure?locale=zh_CN" \
     -H "Authorization: Bearer YOUR_TOKEN_HERE" \
     -o "report.html"
```

### 3. PowerShell

```powershell
# 基本用法
Invoke-WebRequest -Uri "http://localhost:8080/api/reports/196/export/allure?locale=zh_CN" `
                  -OutFile "Allure测试报告.html"

# 带认证
$headers = @{
    "Authorization" = "Bearer YOUR_TOKEN_HERE"
}
Invoke-WebRequest -Uri "http://localhost:8080/api/reports/196/export/allure?locale=zh_CN" `
                  -Headers $headers `
                  -OutFile "report.html"

# 显示进度
Invoke-WebRequest -Uri "http://localhost:8080/api/reports/196/export/allure" `
                  -OutFile "report.html" `
                  -Verbose
```

### 4. Python (requests)

```python
import requests
from datetime import datetime

# 配置
BASE_URL = "http://localhost:8080/api"
REPORT_ID = 196
LOCALE = "zh_CN"
TOKEN = "YOUR_TOKEN_HERE"

# 请求头
headers = {
    "Authorization": f"Bearer {TOKEN}"
}

# 发送请求
url = f"{BASE_URL}/reports/{REPORT_ID}/export/allure"
params = {"locale": LOCALE}

response = requests.get(url, headers=headers, params=params)

# 检查响应
if response.status_code == 200:
    # 生成文件名
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    filename = f"Allure测试报告_{timestamp}.html"
    
    # 保存文件
    with open(filename, 'wb') as f:
        f.write(response.content)
    
    print(f"✓ 报告导出成功: {filename}")
    print(f"  文件大小: {len(response.content)} bytes")
else:
    print(f"✗ 导出失败: {response.status_code}")
    print(f"  错误信息: {response.text}")
```

### 5. JavaScript (Fetch API)

```javascript
// 配置
const BASE_URL = 'http://localhost:8080/api';
const REPORT_ID = 196;
const LOCALE = 'zh_CN';
const TOKEN = 'YOUR_TOKEN_HERE';

// 导出报告
async function exportAllureReport() {
    try {
        const url = `${BASE_URL}/reports/${REPORT_ID}/export/allure?locale=${LOCALE}`;
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${TOKEN}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // 获取文件名
        const contentDisposition = response.headers.get('Content-Disposition');
        const filename = contentDisposition
            ? contentDisposition.split('filename=')[1].replace(/"/g, '')
            : `Allure测试报告_${new Date().getTime()}.html`;

        // 下载文件
        const blob = await response.blob();
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(downloadUrl);
        document.body.removeChild(a);

        console.log('✓ 报告导出成功:', filename);
    } catch (error) {
        console.error('✗ 导出失败:', error);
    }
}

// 调用
exportAllureReport();
```

### 6. Java (Spring RestTemplate)

```java
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AllureReportExporter {
    
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final String TOKEN = "YOUR_TOKEN_HERE";
    
    public static void exportReport(Long reportId, String locale) {
        RestTemplate restTemplate = new RestTemplate();
        
        // 构建URL
        String url = String.format("%s/reports/%d/export/allure?locale=%s", 
                                   BASE_URL, reportId, locale);
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            // 发送请求
            ResponseEntity<Resource> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                Resource.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                // 生成文件名
                String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String filename = "Allure测试报告_" + timestamp + ".html";
                
                // 保存文件
                Resource resource = response.getBody();
                if (resource != null) {
                    try (FileOutputStream fos = new FileOutputStream(filename)) {
                        fos.write(resource.getInputStream().readAllBytes());
                    }
                    System.out.println("✓ 报告导出成功: " + filename);
                }
            } else {
                System.err.println("✗ 导出失败: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("✗ 导出失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        exportReport(196L, "zh_CN");
    }
}
```

### 7. Postman

#### 请求配置
```
Method: GET
URL: http://localhost:8080/api/reports/196/export/allure?locale=zh_CN

Headers:
  Authorization: Bearer YOUR_TOKEN_HERE

Send and Download
```

#### Pre-request Script
```javascript
// 动态设置报告ID
pm.environment.set("reportId", 196);
pm.environment.set("locale", "zh_CN");
```

#### Tests Script
```javascript
// 验证响应
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Content-Type is HTML", function () {
    pm.response.to.have.header("Content-Type");
    pm.expect(pm.response.headers.get("Content-Type")).to.include("text/html");
});

pm.test("File size is reasonable", function () {
    const size = pm.response.stream.length;
    pm.expect(size).to.be.above(1000); // 至少1KB
    console.log("File size:", size, "bytes");
});
```

## 🔐 认证说明

### Bearer Token认证
```bash
# 在请求头中添加
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 获取Token（示例）
```bash
# 登录获取token
curl -X POST "http://localhost:8080/api/auth/login" \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"password123"}' \
     | jq -r '.data.token'
```

## 📊 响应头详解

| 响应头 | 示例值 | 说明 |
|--------|--------|------|
| Content-Type | text/html;charset=UTF-8 | 内容类型为HTML，编码UTF-8 |
| Content-Disposition | attachment; filename="..." | 指定下载文件名 |
| Content-Length | 1234567 | 文件大小（字节） |
| Cache-Control | no-cache, no-store, must-revalidate | 禁用缓存 |
| Pragma | no-cache | HTTP/1.0缓存控制 |
| Expires | 0 | 过期时间 |

## 🐛 错误处理

### 常见错误及解决方案

#### 1. 连接被拒绝
```
Error: connect ECONNREFUSED 127.0.0.1:8080
```
**解决**: 确保服务已启动

#### 2. 401 Unauthorized
```json
{"code": -1, "msg": "未登录或登录已过期"}
```
**解决**: 检查Token是否有效

#### 3. 404 Not Found
```json
{"code": -4, "msg": "报告不存在"}
```
**解决**: 检查报告ID是否正确

#### 4. 500 Internal Server Error
```json
{"code": -5, "msg": "导出报告失败：..."}
```
**解决**: 查看服务器日志，检查数据库连接

## 📝 最佳实践

### 1. 错误处理
```python
try:
    response = requests.get(url, headers=headers, timeout=30)
    response.raise_for_status()
except requests.exceptions.Timeout:
    print("请求超时，请稍后重试")
except requests.exceptions.HTTPError as e:
    print(f"HTTP错误: {e.response.status_code}")
except Exception as e:
    print(f"未知错误: {str(e)}")
```

### 2. 超时设置
```python
# 设置30秒超时
response = requests.get(url, headers=headers, timeout=30)
```

### 3. 重试机制
```python
from requests.adapters import HTTPAdapter
from requests.packages.urllib3.util.retry import Retry

session = requests.Session()
retry = Retry(total=3, backoff_factor=1)
adapter = HTTPAdapter(max_retries=retry)
session.mount('http://', adapter)
session.mount('https://', adapter)

response = session.get(url, headers=headers)
```

### 4. 进度显示
```python
import requests
from tqdm import tqdm

response = requests.get(url, headers=headers, stream=True)
total_size = int(response.headers.get('content-length', 0))

with open(filename, 'wb') as f, tqdm(
    desc=filename,
    total=total_size,
    unit='B',
    unit_scale=True
) as bar:
    for chunk in response.iter_content(chunk_size=8192):
        f.write(chunk)
        bar.update(len(chunk))
```

## 🔗 相关链接

- [快速测试指南](./Allure报告快速测试指南.md)
- [实现总结](./Allure报告实现总结.md)
- [ISO标准报告API](./ISO标准企业级报告完整文档.md)

---

**版本**: v1.0.0  
**更新时间**: 2024-10-26  
**作者**: Victor

