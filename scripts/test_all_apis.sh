#!/bin/bash

# TestExecutionController 接口测试脚本
# 用于快速测试所有接口是否可用

BASE_URL="http://localhost:8080/api"
BOLD='\033[1m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BOLD}======================================${NC}"
echo -e "${BOLD}TestExecutionController 接口测试${NC}"
echo -e "${BOLD}======================================${NC}"
echo ""

# 测试计数器
TOTAL=0
SUCCESS=0
FAILED=0

# 测试函数
test_api() {
    local name=$1
    local method=$2
    local url=$3
    local data=$4
    
    TOTAL=$((TOTAL + 1))
    echo -e "${YELLOW}[测试 $TOTAL] $name${NC}"
    echo -e "请求: $method $url"
    
    if [ -z "$data" ]; then
        # GET 请求
        response=$(curl -s -w "\n%{http_code}" -X $method "$url")
    else
        # POST/PUT/DELETE 请求
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" \
            -H "Content-Type: application/json" \
            -d "$data")
    fi
    
    # 提取 HTTP 状态码
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    # 判断是否成功
    if [[ $http_code =~ ^(200|201)$ ]]; then
        echo -e "${GREEN}✓ 成功 (HTTP $http_code)${NC}"
        SUCCESS=$((SUCCESS + 1))
    else
        echo -e "${RED}✗ 失败 (HTTP $http_code)${NC}"
        FAILED=$((FAILED + 1))
    fi
    
    # 显示响应体（截断显示）
    echo "响应: $(echo $body | cut -c1-200)..."
    echo ""
}

echo -e "${BOLD}========== 测试用例执行相关接口 ==========${NC}"
echo ""

# 1. 执行单个测试用例
test_api "执行单个测试用例" \
    "POST" \
    "$BASE_URL/test-cases/1/execute" \
    '{"environment":"test","timeout":30000}'

# 2. 异步执行测试用例
test_api "异步执行测试用例" \
    "POST" \
    "$BASE_URL/test-cases/1/execute-async" \
    '{"environment":"test","async":true}'

# 3. 查询任务状态
test_api "查询任务状态" \
    "GET" \
    "$BASE_URL/tasks/test-task-id-123/status"

# 4. 取消任务执行
test_api "取消任务执行" \
    "POST" \
    "$BASE_URL/tasks/test-task-id-123/cancel" \
    '{}'

# 5. 获取执行结果详情
test_api "获取执行结果详情" \
    "GET" \
    "$BASE_URL/test-results/1"

# 6. 获取执行日志
test_api "获取执行日志" \
    "GET" \
    "$BASE_URL/test-results/1/logs"

# 7. 生成测试报告
test_api "生成测试报告" \
    "POST" \
    "$BASE_URL/test-results/1/report" \
    '{}'

echo -e "${BOLD}========== 模块执行相关接口 ==========${NC}"
echo ""

# 8. 执行模块测试（同步）
test_api "执行模块测试（同步）" \
    "POST" \
    "$BASE_URL/modules/1/execute" \
    '{"environment":"test","concurrency":5}'

# 9. 异步执行模块测试
test_api "异步执行模块测试" \
    "POST" \
    "$BASE_URL/modules/1/execute-async" \
    '{"environment":"test","async":true}'

# 10. 查询模块任务状态
test_api "查询模块任务状态" \
    "GET" \
    "$BASE_URL/module-tasks/module-task-123/status"

# 11. 取消模块任务执行
test_api "取消模块任务执行" \
    "POST" \
    "$BASE_URL/module-tasks/module-task-123/cancel" \
    '{}'

echo -e "${BOLD}========== 项目执行相关接口 ==========${NC}"
echo ""

# 12. 执行项目测试
test_api "执行项目测试" \
    "POST" \
    "$BASE_URL/projects/1/execute" \
    '{"environment":"test","concurrency":10}'

# 13. 异步执行项目测试
test_api "异步执行项目测试" \
    "POST" \
    "$BASE_URL/projects/1/execute-async" \
    '{"environment":"test","async":true}'

# 14. 查询项目任务状态
test_api "查询项目任务状态" \
    "GET" \
    "$BASE_URL/project-tasks/project-task-123/status"

# 15. 取消项目任务执行
test_api "取消项目任务执行" \
    "POST" \
    "$BASE_URL/project-tasks/project-task-123/cancel" \
    '{}'

echo -e "${BOLD}========== 接口执行相关接口 ==========${NC}"
echo ""

# 16. 执行接口测试
test_api "执行接口测试" \
    "POST" \
    "$BASE_URL/apis/1/execute" \
    '{"environment":"test","concurrency":3}'

# 17. 异步执行接口测试
test_api "异步执行接口测试" \
    "POST" \
    "$BASE_URL/apis/1/execute-async" \
    '{"environment":"test","async":true}'

# 18. 查询接口任务状态
test_api "查询接口任务状态" \
    "GET" \
    "$BASE_URL/api-tasks/api-task-123/status"

# 19. 取消接口任务执行
test_api "取消接口任务执行" \
    "POST" \
    "$BASE_URL/api-tasks/api-task-123/cancel" \
    '{}'

echo -e "${BOLD}========== 测试套件执行相关接口 ==========${NC}"
echo ""

# 20. 执行测试套件
test_api "执行测试套件" \
    "POST" \
    "$BASE_URL/test-suites/1/execute" \
    '{"environment":"test","concurrency":5}'

# 21. 异步执行测试套件
test_api "异步执行测试套件" \
    "POST" \
    "$BASE_URL/test-suites/1/execute-async" \
    '{"environment":"test","async":true}'

# 22. 查询测试套件任务状态
test_api "查询测试套件任务状态" \
    "GET" \
    "$BASE_URL/suite-tasks/suite-task-123/status"

# 23. 取消测试套件任务执行
test_api "取消测试套件任务执行" \
    "POST" \
    "$BASE_URL/suite-tasks/suite-task-123/cancel" \
    '{}'

echo -e "${BOLD}========== 测试结果查询相关接口 ==========${NC}"
echo ""

# 24. 分页获取测试结果列表（无参数）
test_api "获取测试结果列表（无参数）" \
    "GET" \
    "$BASE_URL/test-results"

# 25. 分页获取测试结果列表（带分页）
test_api "获取测试结果列表（带分页）" \
    "GET" \
    "$BASE_URL/test-results?page=1&page_size=10"

# 26. 查询失败的测试结果
test_api "查询失败的测试结果" \
    "GET" \
    "$BASE_URL/test-results?status=failed&page=1&page_size=20"

# 27. 按任务类型查询
test_api "按任务类型查询" \
    "GET" \
    "$BASE_URL/test-results?task_type=test_case&ref_id=101"

# 28. 复杂条件查询
test_api "复杂条件查询" \
    "GET" \
    "$BASE_URL/test-results?task_type=test_case&status=passed&priority=P0,P1&environment=test&sort_by=start_time&sort_order=desc"

# 29. 关键字搜索
test_api "关键字搜索" \
    "GET" \
    "$BASE_URL/test-results?search_keyword=登录&page=1&page_size=10"

echo ""
echo -e "${BOLD}======================================${NC}"
echo -e "${BOLD}测试结果统计${NC}"
echo -e "${BOLD}======================================${NC}"
echo -e "总计: ${BOLD}$TOTAL${NC} 个接口"
echo -e "成功: ${GREEN}$SUCCESS${NC} 个"
echo -e "失败: ${RED}$FAILED${NC} 个"
echo -e "成功率: ${BOLD}$(awk "BEGIN {printf \"%.2f\", ($SUCCESS/$TOTAL)*100}")%${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}${BOLD}✓ 所有接口测试通过！${NC}"
    exit 0
else
    echo -e "${RED}${BOLD}✗ 有 $FAILED 个接口测试失败，请检查！${NC}"
    exit 1
fi

