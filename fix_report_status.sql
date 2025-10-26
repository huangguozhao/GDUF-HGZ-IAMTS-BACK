-- 修复报告状态问题
-- 将状态为 'generating' 但实际已有数据的报告状态更新为 'completed'

-- 查看需要修复的报告
SELECT 
    report_id,
    report_name,
    report_status,
    total_cases,
    success_rate,
    start_time,
    end_time
FROM TestReportSummaries
WHERE report_status = 'generating'
  AND total_cases > 0
  AND is_deleted = 0;

-- 修复报告状态
UPDATE TestReportSummaries
SET 
    report_status = 'completed',
    updated_at = NOW()
WHERE report_status = 'generating'
  AND total_cases > 0
  AND is_deleted = 0;

-- 验证修复结果
SELECT 
    report_id,
    report_name,
    report_status,
    total_cases,
    updated_at
FROM TestReportSummaries
WHERE report_id = 196;

