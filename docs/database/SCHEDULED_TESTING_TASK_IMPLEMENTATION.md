# 定时测试任务功能完整实现方案

## 1. 概述

本文档提供了一个完整的定时测试任务功能实现方案，用户可以通过此功能制定定期执行的测试计划。系统将在指定的时间自动执行测试，并记录执行结果。

### 核心功能
- **创建定时任务**: 用户可以创建按照指定时间表自动执行测试的任务
- **灵活的调度方式**: 支持每日、每周、每月、自定义Cron表达式等多种触发方式
- **执行范围**: 支持单个测试用例、模块、项目等多个执行粒度
- **任务管理**: 启用/禁用、编辑、删除、查看执行历史
- **执行追踪**: 记录每次执行的详细信息和结果统计
- **触发记录**: 在TestExecutionRecords表中记录触发任务的信息

---

## 2. 技术架构

### 2.1 核心组件

```
定时测试任务系统
├── 前端界面 (UI)
│   ├── 创建/编辑定时任务
│   ├── 任务列表和管理
│   ├── 执行历史查看
│   └── 任务监控
│
├── API层 (ScheduledTaskController)
│   ├── POST /api/scheduled-tasks - 创建任务
│   ├── PUT /api/scheduled-tasks/{id} - 编辑任务
│   ├── DELETE /api/scheduled-tasks/{id} - 删除任务
│   ├── GET /api/scheduled-tasks - 列表查询
│   ├── GET /api/scheduled-tasks/{id} - 详情查询
│   ├── POST /api/scheduled-tasks/{id}/enable - 启用任务
│   ├── POST /api/scheduled-tasks/{id}/disable - 禁用任务
│   ├── POST /api/scheduled-tasks/{id}/execute - 立即执行
│   ├── GET /api/scheduled-tasks/{id}/history - 执行历史
│   └── GET /api/scheduled-tasks/{id}/statistics - 执行统计
│
├── 业务层 (Service)
│   ├── ScheduledTaskService - 任务管理服务
│   ├── ScheduledTaskExecutionService - 任务执行服务
│   └── ScheduledTaskHistoryService - 历史追踪服务
│
├── 调度层 (Quartz)
│   ├── ScheduledTestTaskJob - 定时任务执行器
│   ├── QuartzSchedulerConfig - Quartz配置
│   └── TaskTriggerManager - 触发器管理器
│
├── 数据层 (Mapper)
│   ├── ScheduledTaskMapper - 任务查询
│   ├── ScheduledTaskExecutionMapper - 执行记录
│   └── ScheduledTaskHistoryMapper - 历史查询
│
└── 数据库 (MySQL)
    ├── ScheduledTestTasks - 任务定义表
    ├── ScheduledTaskExecutions - 任务执行记录表
    └── TestExecutionRecords - 关联执行记录表
```

### 2.2 工作流程

```
1. 用户创建定时任务
   ↓
2. 系统验证参数并创建Quartz触发器
   ↓
3. 任务信息保存到数据库
   ↓
4. 在指定时间Quartz自动触发任务
   ↓
5. 执行对应的TestExecutionService中的执行方法
   ↓
6. 记录执行结果到ScheduledTaskExecutions和TestExecutionRecords
   ↓
7. 生成测试报告
   ↓
8. 用户可查看执行历史和统计信息
```

---

## 3. 数据库设计

### 3.1 ScheduledTestTasks 表 - 定时任务定义表

```sql
CREATE TABLE ScheduledTestTasks (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    
    -- 基本信息
    task_name VARCHAR(255) NOT NULL COMMENT '任务名称',
    description TEXT COMMENT '任务描述',
    task_type ENUM('single_case', 'module', 'project', 'test_suite', 'api') NOT NULL COMMENT '任务类型',
    
    -- 执行目标
    target_id INT NOT NULL COMMENT '目标ID（取决于task_type）',
    target_name VARCHAR(255) NOT NULL COMMENT '目标名称',
    
    -- 调度配置
    trigger_type ENUM('cron', 'simple', 'daily', 'weekly', 'monthly') NOT NULL COMMENT '触发器类型',
    cron_expression VARCHAR(255) COMMENT 'Cron表达式（trigger_type=cron时必填）',
    simple_repeat_interval INT COMMENT '重复间隔（毫秒，trigger_type=simple时使用）',
    simple_repeat_count INT DEFAULT -1 COMMENT '重复次数（-1表示无限）',
    daily_hour INT COMMENT '每日执行时刻-小时（0-23）',
    daily_minute INT COMMENT '每日执行时刻-分钟（0-59）',
    weekly_days VARCHAR(50) COMMENT '每周执行的天数（1-7，逗号分隔，1=周一）',
    monthly_day INT COMMENT '每月执行的日期（1-31）',
    
    -- 执行配置
    execution_environment ENUM('dev', 'test', 'prod', 'staging') NOT NULL DEFAULT 'test' COMMENT '执行环境',
    base_url VARCHAR(500) COMMENT '基础URL覆盖',
    timeout_seconds INT DEFAULT 30 COMMENT '超时时间（秒）',
    concurrency INT DEFAULT 1 COMMENT '并发数',
    execution_strategy ENUM('sequential', 'parallel', 'by_module', 'smart') DEFAULT 'sequential' COMMENT '执行策略',
    retry_enabled TINYINT(1) DEFAULT 0 COMMENT '是否启用重试',
    max_retry_attempts INT DEFAULT 0 COMMENT '最大重试次数',
    retry_delay_ms INT DEFAULT 1000 COMMENT '重试延迟（毫秒）',
    
    -- 通知配置
    notify_on_success TINYINT(1) DEFAULT 0 COMMENT '成功时通知',
    notify_on_failure TINYINT(1) DEFAULT 1 COMMENT '失败时通知',
    notification_recipients VARCHAR(500) COMMENT '通知接收者（邮箱，逗号分隔）',
    
    -- 执行限制
    skip_if_previous_failed TINYINT(1) DEFAULT 0 COMMENT '前次失败时跳过',
    max_duration_seconds INT COMMENT '最大执行时长（秒）',
    
    -- 状态信息
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    
    -- 执行统计
    total_executions INT DEFAULT 0 COMMENT '总执行次数',
    successful_executions INT DEFAULT 0 COMMENT '成功执行次数',
    failed_executions INT DEFAULT 0 COMMENT '失败执行次数',
    skipped_executions INT DEFAULT 0 COMMENT '跳过执行次数',
    
    -- 时间信息
    next_trigger_time DATETIME COMMENT '下次触发时间',
    last_execution_time DATETIME COMMENT '最后一次执行时间',
    last_execution_status ENUM('pending', 'running', 'success', 'failed', 'skipped') COMMENT '最后执行状态',
    
    -- 创建和修改信息
    created_by INT NOT NULL COMMENT '创建人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by INT COMMENT '最后修改人ID',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    deleted_by INT COMMENT '删除人ID',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    
    -- 索引
    UNIQUE KEY uk_task_name (task_name, is_deleted),
    INDEX idx_task_type (task_type),
    INDEX idx_target_id (target_id),
    INDEX idx_is_enabled (is_enabled),
    INDEX idx_next_trigger_time (next_trigger_time),
    INDEX idx_created_by (created_by),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (created_by) REFERENCES Users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时测试任务定义表';
```

### 3.2 ScheduledTaskExecutions 表 - 定时任务执行记录表

```sql
CREATE TABLE ScheduledTaskExecutions (
    execution_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '执行记录ID',
    
    -- 关联信息
    task_id BIGINT NOT NULL COMMENT '任务ID，关联ScheduledTestTasks',
    test_execution_record_id BIGINT COMMENT '测试执行记录ID，关联TestExecutionRecords',
    quartz_job_id VARCHAR(255) COMMENT 'Quartz任务ID',
    
    -- 执行状态
    status ENUM('pending', 'running', 'success', 'failed', 'skipped', 'timeout', 'cancelled') NOT NULL DEFAULT 'pending' COMMENT '执行状态',
    
    -- 时间信息
    scheduled_time DATETIME NOT NULL COMMENT '计划执行时间',
    actual_start_time DATETIME COMMENT '实际开始时间',
    actual_end_time DATETIME COMMENT '实际结束时间',
    duration_seconds INT COMMENT '执行耗时（秒）',
    delay_seconds INT COMMENT '延迟时间（秒，actual_start_time - scheduled_time）',
    
    -- 执行统计
    total_cases INT DEFAULT 0 COMMENT '总用例数',
    passed_cases INT DEFAULT 0 COMMENT '通过数',
    failed_cases INT DEFAULT 0 COMMENT '失败数',
    skipped_cases INT DEFAULT 0 COMMENT '跳过数',
    success_rate DECIMAL(5,2) DEFAULT 0 COMMENT '成功率',
    
    -- 错误信息
    error_message TEXT COMMENT '错误信息',
    stack_trace TEXT COMMENT '堆栈跟踪',
    
    -- 重试信息
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    is_retry TINYINT(1) DEFAULT 0 COMMENT '是否是重试',
    original_execution_id BIGINT COMMENT '原始执行ID（如果是重试）',
    
    -- 通知状态
    notification_sent TINYINT(1) DEFAULT 0 COMMENT '是否发送通知',
    notification_channels VARCHAR(100) COMMENT '通知渠道（email,webhook等）',
    notification_timestamp DATETIME COMMENT '通知时间',
    
    -- 审计字段
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    
    -- 索引
    INDEX idx_task_id (task_id),
    INDEX idx_test_execution_record_id (test_execution_record_id),
    INDEX idx_status (status),
    INDEX idx_scheduled_time (scheduled_time),
    INDEX idx_actual_start_time (actual_start_time),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (task_id) REFERENCES ScheduledTestTasks(task_id),
    FOREIGN KEY (test_execution_record_id) REFERENCES TestExecutionRecords(record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务执行记录表';
```

### 3.3 修改 TestExecutionRecords 表

在现有 `TestExecutionRecords` 表的基础上，添加一个字段用于关联定时任务：

```sql
-- 如果还没有这个字段，执行以下语句
ALTER TABLE TestExecutionRecords 
ADD COLUMN scheduled_task_id BIGINT NULL COMMENT '关联的定时任务ID' 
ADD CONSTRAINT fk_scheduled_task_id FOREIGN KEY (scheduled_task_id) REFERENCES ScheduledTestTasks(task_id),
ADD INDEX idx_scheduled_task_id (scheduled_task_id);
```

---

## 4. 实体类和DTO设计

### 4.1 Entity 类

```java
// ScheduledTestTask.java - 定时任务实体
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ScheduledTestTasks")
public class ScheduledTestTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;
    
    private String taskName;
    private String description;
    private String taskType; // single_case, module, project, test_suite, api
    
    private Integer targetId;
    private String targetName;
    
    private String triggerType; // cron, simple, daily, weekly, monthly
    private String cronExpression;
    private Integer simpleRepeatInterval;
    private Integer simpleRepeatCount;
    private Integer dailyHour;
    private Integer dailyMinute;
    private String weeklyDays;
    private Integer monthlyDay;
    
    private String executionEnvironment; // dev, test, prod, staging
    private String baseUrl;
    private Integer timeoutSeconds;
    private Integer concurrency;
    private String executionStrategy;
    private Boolean retryEnabled;
    private Integer maxRetryAttempts;
    private Integer retryDelayMs;
    
    private Boolean notifyOnSuccess;
    private Boolean notifyOnFailure;
    private String notificationRecipients;
    
    private Boolean skipIfPreviousFailed;
    private Integer maxDurationSeconds;
    
    private Boolean isEnabled;
    private Boolean isDeleted;
    
    private Integer totalExecutions;
    private Integer successfulExecutions;
    private Integer failedExecutions;
    private Integer skippedExecutions;
    
    private LocalDateTime nextTriggerTime;
    private LocalDateTime lastExecutionTime;
    private String lastExecutionStatus;
    
    private Integer createdBy;
    private LocalDateTime createdAt;
    private Integer updatedBy;
    private LocalDateTime updatedAt;
    private Integer deletedBy;
    private LocalDateTime deletedAt;
}

// ScheduledTaskExecution.java - 定时任务执行记录实体
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ScheduledTaskExecutions")
public class ScheduledTaskExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executionId;
    
    private Long taskId;
    private Long testExecutionRecordId;
    private String quartzJobId;
    
    private String status; // pending, running, success, failed, skipped, timeout, cancelled
    
    private LocalDateTime scheduledTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private Integer durationSeconds;
    private Integer delaySeconds;
    
    private Integer totalCases;
    private Integer passedCases;
    private Integer failedCases;
    private Integer skippedCases;
    private BigDecimal successRate;
    
    private String errorMessage;
    private String stackTrace;
    
    private Integer retryCount;
    private Boolean isRetry;
    private Long originalExecutionId;
    
    private Boolean notificationSent;
    private String notificationChannels;
    private LocalDateTime notificationTimestamp;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 4.2 DTO 类

```java
// CreateScheduledTaskDTO.java - 创建任务请求
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduledTaskDTO {
    @NotBlank(message = "任务名称不能为空")
    private String taskName;
    
    private String description;
    
    @NotBlank(message = "任务类型不能为空")
    private String taskType; // single_case, module, project, test_suite, api
    
    @NotNull(message = "目标ID不能为空")
    private Integer targetId;
    
    @NotBlank(message = "触发器类型不能为空")
    private String triggerType; // cron, simple, daily, weekly, monthly
    
    // 条件字段根据triggerType
    private String cronExpression;
    private Integer simpleRepeatInterval;
    private Integer simpleRepeatCount;
    private Integer dailyHour;
    private Integer dailyMinute;
    private String weeklyDays;
    private Integer monthlyDay;
    
    private String executionEnvironment; // dev, test, prod, staging
    private String baseUrl;
    private Integer timeoutSeconds;
    private Integer concurrency;
    private String executionStrategy;
    
    private Boolean retryEnabled;
    private Integer maxRetryAttempts;
    private Integer retryDelayMs;
    
    private Boolean notifyOnSuccess;
    private Boolean notifyOnFailure;
    private String notificationRecipients;
    
    private Boolean skipIfPreviousFailed;
    private Integer maxDurationSeconds;
}

// UpdateScheduledTaskDTO.java - 更新任务请求
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduledTaskDTO extends CreateScheduledTaskDTO {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
}

// ScheduledTaskDTO.java - 任务详情响应
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTaskDTO {
    private Long taskId;
    private String taskName;
    private String description;
    private String taskType;
    private Integer targetId;
    private String targetName;
    
    private String triggerType;
    private String cronExpression;
    private String executionEnvironment;
    
    private Boolean isEnabled;
    private Integer totalExecutions;
    private Integer successfulExecutions;
    private Integer failedExecutions;
    
    private LocalDateTime nextTriggerTime;
    private LocalDateTime lastExecutionTime;
    private String lastExecutionStatus;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// ScheduledTaskExecutionDTO.java - 执行记录响应
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTaskExecutionDTO {
    private Long executionId;
    private Long taskId;
    private String status;
    
    private LocalDateTime scheduledTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private Integer durationSeconds;
    
    private Integer totalCases;
    private Integer passedCases;
    private Integer failedCases;
    private BigDecimal successRate;
    
    private String errorMessage;
    private LocalDateTime createdAt;
}
```

---

## 5. 关键实现步骤

### 5.1 启用Quartz调度器

```yaml
# application.yml
spring:
  quartz:
    job-store-type: jdbc
    jdbc:
      initialize-schema: always
    properties:
      org:
        quartz:
          scheduler:
            instanceName: IATMScheduler
            instanceId: AUTO
          threadPool:
            class: org.quartz.simpl.SimpleThreadPool
            threadCount: 10
            threadsInheritContextClassLoaderOfInitializingThread: true
          jobStore:
            class: org.quartz.impl.jdbcjobstore.JobStoreTX
            driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
            tablePrefix: QRTZ_
            isClustered: false
            useProperties: false
```

### 5.2 创建Quartz Job执行器

```java
// ScheduledTestTaskJob.java
@Component
public class ScheduledTestTaskJob implements Job {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTestTaskJob.class);
    
    @Autowired
    private ScheduledTaskService scheduledTaskService;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // 从JobDetail中获取任务ID
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            Long taskId = dataMap.getLong("taskId");
            Integer userId = dataMap.getInt("userId");
            
            logger.info("开始执行定时任务: taskId={}", taskId);
            
            // 执行任务
            scheduledTaskService.executeScheduledTask(taskId, userId, true);
            
            logger.info("定时任务执行完成: taskId={}", taskId);
        } catch (Exception e) {
            logger.error("定时任务执行失败", e);
            throw new JobExecutionException(e);
        }
    }
}
```

### 5.3 创建Quartz配置类

```java
// QuartzSchedulerConfig.java
@Configuration
public class QuartzSchedulerConfig {
    
    @Bean
    public Scheduler quartzScheduler(SchedulerFactoryBean schedulerFactoryBean) {
        return schedulerFactoryBean.getScheduler();
    }
    
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setApplicationContextSchedulerContextKey("applicationContext");
        
        Properties properties = new Properties();
        properties.setProperty("org.quartz.scheduler.instanceName", "IATMScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.setProperty("org.quartz.threadPool.threadCount", "10");
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.setProperty("org.quartz.jobStore.driverDelegateClass", 
            "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        properties.setProperty("org.quartz.jobStore.isClustered", "false");
        
        bean.setQuartzProperties(properties);
        bean.setWaitForJobsToCompleteOnShutdown(true);
        bean.setAutoStartup(true);
        
        return bean;
    }
}
```

### 5.4 Service实现关键方法

```java
// ScheduledTaskService.java - 核心方法
public interface ScheduledTaskService {
    
    // 任务管理
    ScheduledTaskDTO createScheduledTask(CreateScheduledTaskDTO dto, Integer userId);
    ScheduledTaskDTO updateScheduledTask(UpdateScheduledTaskDTO dto, Integer userId);
    void deleteScheduledTask(Long taskId, Integer userId);
    ScheduledTaskDTO getScheduledTask(Long taskId, Integer userId);
    Page<ScheduledTaskDTO> listScheduledTasks(ScheduledTaskQueryDTO query, Integer userId);
    
    // 任务控制
    void enableScheduledTask(Long taskId, Integer userId);
    void disableScheduledTask(Long taskId, Integer userId);
    void executeScheduledTaskNow(Long taskId, Integer userId);
    
    // 内部执行（Quartz调用）
    void executeScheduledTask(Long taskId, Integer userId, boolean isTriggered);
    
    // 执行历史
    Page<ScheduledTaskExecutionDTO> getExecutionHistory(Long taskId, int page, int size);
    ScheduledTaskExecutionStatisticsDTO getExecutionStatistics(Long taskId);
    
    // 触发器管理
    void updateNextTriggerTime(Long taskId);
}

// ScheduledTaskServiceImpl.java - 执行定时任务的核心实现
@Service
public class ScheduledTaskServiceImpl implements ScheduledTaskService {
    
    @Autowired
    private ScheduledTaskMapper scheduledTaskMapper;
    
    @Autowired
    private ScheduledTaskExecutionMapper executionMapper;
    
    @Autowired
    private TestExecutionService testExecutionService;
    
    @Autowired
    private TestExecutionRecordMapper executionRecordMapper;
    
    @Autowired
    private Scheduler scheduler;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeScheduledTask(Long taskId, Integer userId, boolean isTriggered) {
        LocalDateTime startTime = LocalDateTime.now();
        ScheduledTestTask task = scheduledTaskMapper.selectByPrimaryKey(taskId);
        
        if (task == null || !task.getIsEnabled()) {
            return;
        }
        
        // 1. 创建执行记录
        ScheduledTaskExecution execution = new ScheduledTaskExecution();
        execution.setTaskId(taskId);
        execution.setScheduledTime(startTime);
        execution.setStatus("running");
        executionMapper.insert(execution);
        
        try {
            execution.setActualStartTime(LocalDateTime.now());
            
            // 2. 根据任务类型调用对应的执行方法
            switch (task.getTaskType()) {
                case "single_case":
                    executeSingleCase(task, execution, userId);
                    break;
                case "module":
                    executeModule(task, execution, userId);
                    break;
                case "project":
                    executeProject(task, execution, userId);
                    break;
                case "test_suite":
                    executeTestSuite(task, execution, userId);
                    break;
                case "api":
                    executeApi(task, execution, userId);
                    break;
            }
            
            // 3. 更新执行记录
            execution.setActualEndTime(LocalDateTime.now());
            execution.setDurationSeconds((int)(
                (execution.getActualEndTime().getTime() - 
                 execution.getActualStartTime().getTime()) / 1000));
            execution.setStatus("success");
            executionMapper.updateByPrimaryKeySelective(execution);
            
            // 4. 更新任务的执行统计
            task.setTotalExecutions(task.getTotalExecutions() + 1);
            task.setSuccessfulExecutions(task.getSuccessfulExecutions() + 1);
            task.setLastExecutionTime(execution.getActualEndTime());
            task.setLastExecutionStatus("success");
            scheduledTaskMapper.updateByPrimaryKeySelective(task);
            
        } catch (Exception e) {
            // 记录失败
            execution.setActualEndTime(LocalDateTime.now());
            execution.setDurationSeconds((int)(
                (execution.getActualEndTime().getTime() - 
                 execution.getActualStartTime().getTime()) / 1000));
            execution.setStatus("failed");
            execution.setErrorMessage(e.getMessage());
            executionMapper.updateByPrimaryKeySelective(execution);
            
            task.setTotalExecutions(task.getTotalExecutions() + 1);
            task.setFailedExecutions(task.getFailedExecutions() + 1);
            task.setLastExecutionTime(execution.getActualEndTime());
            task.setLastExecutionStatus("failed");
            scheduledTaskMapper.updateByPrimaryKeySelective(task);
            
            // 发送失败通知
            if (task.getNotifyOnFailure()) {
                sendNotification(task, "FAILED", e.getMessage());
            }
        }
        
        // 更新下次触发时间
        updateNextTriggerTime(taskId);
    }
    
    private void executeSingleCase(ScheduledTestTask task, 
            ScheduledTaskExecution execution, Integer userId) {
        ExecuteTestCaseDTO dto = buildExecuteDTO(task);
        ExecutionResultDTO result = testExecutionService.executeTestCase(
            task.getTargetId(), dto, userId);
        updateExecutionFromResult(execution, result);
    }
    
    private void executeModule(ScheduledTestTask task, 
            ScheduledTaskExecution execution, Integer userId) {
        ExecuteModuleDTO dto = buildExecuteModuleDTO(task);
        ModuleExecutionResultDTO result = testExecutionService.executeModule(
            task.getTargetId(), dto, userId);
        updateExecutionFromModuleResult(execution, result);
    }
    
    private void executeProject(ScheduledTestTask task, 
            ScheduledTaskExecution execution, Integer userId) {
        ExecuteProjectDTO dto = buildExecuteProjectDTO(task);
        ProjectExecutionResultDTO result = testExecutionService.executeProject(
            task.getTargetId(), dto, userId);
        updateExecutionFromProjectResult(execution, result);
    }
    
    // 其他执行方法...
    
    private ExecuteTestCaseDTO buildExecuteDTO(ScheduledTestTask task) {
        ExecuteTestCaseDTO dto = new ExecuteTestCaseDTO();
        dto.setEnvironment(task.getExecutionEnvironment());
        dto.setBaseUrl(task.getBaseUrl());
        dto.setTimeout(task.getTimeoutSeconds());
        return dto;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createQuartzJob(ScheduledTestTask task) throws SchedulerException {
        // 构建Job
        JobDetail jobDetail = JobBuilder.newJob(ScheduledTestTaskJob.class)
            .withIdentity("scheduled_task_" + task.getTaskId(), "scheduled_tasks")
            .usingJobData("taskId", task.getTaskId())
            .usingJobData("userId", task.getCreatedBy())
            .build();
        
        // 构建Trigger
        Trigger trigger = buildTrigger(task);
        
        // 提交到调度器
        scheduler.scheduleJob(jobDetail, trigger);
    }
    
    private Trigger buildTrigger(ScheduledTestTask task) {
        String triggerId = "scheduled_task_trigger_" + task.getTaskId();
        
        switch (task.getTriggerType()) {
            case "cron":
                return TriggerBuilder.newTrigger()
                    .withIdentity(triggerId, "scheduled_tasks")
                    .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression()))
                    .build();
                
            case "daily":
                return TriggerBuilder.newTrigger()
                    .withIdentity(triggerId, "scheduled_tasks")
                    .withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
                        .onEveryDay()
                        .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(
                            task.getDailyHour(), task.getDailyMinute())))
                    .build();
                
            case "weekly":
                DayOfWeek[] days = Arrays.stream(task.getWeeklyDays().split(","))
                    .map(Integer::parseInt)
                    .map(d -> DayOfWeek.values()[d - 1])
                    .toArray(DayOfWeek[]::new);
                return TriggerBuilder.newTrigger()
                    .withIdentity(triggerId, "scheduled_tasks")
                    .withSchedule(WeeklyScheduleBuilder.weeklySchedule()
                        .onDaysOfTheWeek(days)
                        .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(
                            task.getDailyHour(), task.getDailyMinute())))
                    .build();
                
            case "monthly":
                return TriggerBuilder.newTrigger()
                    .withIdentity(triggerId, "scheduled_tasks")
                    .withSchedule(MonthlyScheduleBuilder.monthlyOnDayAndHourAndMinute(
                        task.getMonthlyDay(), task.getDailyHour(), task.getDailyMinute()))
                    .build();
                
            default:
                throw new IllegalArgumentException("不支持的触发器类型: " + task.getTriggerType());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuartzJob(Long taskId) throws SchedulerException {
        scheduler.deleteJob(new JobKey("scheduled_task_" + taskId, "scheduled_tasks"));
    }
    
    @Override
    public void updateNextTriggerTime(Long taskId) {
        ScheduledTestTask task = scheduledTaskMapper.selectByPrimaryKey(taskId);
        try {
            // 从Quartz获取下次触发时间
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(
                new JobKey("scheduled_task_" + taskId, "scheduled_tasks"));
            if (!triggers.isEmpty()) {
                Trigger trigger = triggers.get(0);
                task.setNextTriggerTime(
                    LocalDateTime.ofInstant(
                        trigger.getNextFireTime().toInstant(), 
                        ZoneId.systemDefault()));
                scheduledTaskMapper.updateByPrimaryKeySelective(task);
            }
        } catch (SchedulerException e) {
            logger.error("更新下次触发时间失败: {}", e.getMessage());
        }
    }
}
```

---

## 6. API接口设计

### 6.1 创建定时任务

```
POST /api/scheduled-tasks
Content-Type: application/json
Authorization: Bearer {token}

请求体:
{
  "taskName": "每日API自动化测试",
  "description": "每天早上9点执行用户管理模块的所有测试",
  "taskType": "module",
  "targetId": 1,
  "triggerType": "daily",
  "dailyHour": 9,
  "dailyMinute": 0,
  "executionEnvironment": "test",
  "concurrency": 3,
  "executionStrategy": "by_module",
  "retryEnabled": true,
  "maxRetryAttempts": 2,
  "notifyOnFailure": true,
  "notificationRecipients": "test@example.com"
}

响应:
{
  "code": 1,
  "msg": "定时任务创建成功",
  "data": {
    "taskId": 1001,
    "taskName": "每日API自动化测试",
    "isEnabled": true,
    "nextTriggerTime": "2024-09-16T09:00:00",
    "createdAt": "2024-09-15T14:30:00"
  }
}
```

### 6.2 更新定时任务

```
PUT /api/scheduled-tasks/{id}
Content-Type: application/json
Authorization: Bearer {token}

请求体: 同创建接口

响应: 返回更新后的任务信息
```

### 6.3 删除定时任务

```
DELETE /api/scheduled-tasks/{id}
Authorization: Bearer {token}

响应:
{
  "code": 1,
  "msg": "定时任务删除成功"
}
```

### 6.4 列表查询

```
GET /api/scheduled-tasks?page=1&size=10&taskType=module&isEnabled=true
Authorization: Bearer {token}

响应:
{
  "code": 1,
  "msg": "查询成功",
  "data": {
    "content": [
      {
        "taskId": 1001,
        "taskName": "每日API自动化测试",
        "taskType": "module",
        "targetName": "用户管理模块",
        "executionEnvironment": "test",
        "isEnabled": true,
        "totalExecutions": 15,
        "successfulExecutions": 14,
        "failedExecutions": 1,
        "nextTriggerTime": "2024-09-16T09:00:00",
        "lastExecutionTime": "2024-09-15T09:00:00",
        "lastExecutionStatus": "success"
      }
    ],
    "totalElements": 45,
    "totalPages": 5,
    "currentPage": 1
  }
}
```

### 6.5 启用/禁用

```
POST /api/scheduled-tasks/{id}/enable
POST /api/scheduled-tasks/{id}/disable
Authorization: Bearer {token}

响应:
{
  "code": 1,
  "msg": "操作成功"
}
```

### 6.6 立即执行

```
POST /api/scheduled-tasks/{id}/execute
Authorization: Bearer {token}

响应:
{
  "code": 1,
  "msg": "任务已提交执行",
  "data": {
    "executionId": 10001,
    "taskId": 1001,
    "status": "running",
    "scheduledTime": "2024-09-15T14:35:00",
    "actualStartTime": "2024-09-15T14:35:02"
  }
}
```

### 6.7 执行历史

```
GET /api/scheduled-tasks/{id}/history?page=1&size=10
Authorization: Bearer {token}

响应:
{
  "code": 1,
  "msg": "查询成功",
  "data": {
    "content": [
      {
        "executionId": 10015,
        "taskId": 1001,
        "status": "success",
        "scheduledTime": "2024-09-15T09:00:00",
        "actualStartTime": "2024-09-15T09:00:02",
        "actualEndTime": "2024-09-15T09:05:30",
        "durationSeconds": 328,
        "totalCases": 25,
        "passedCases": 25,
        "failedCases": 0,
        "successRate": 100.00
      }
    ],
    "totalElements": 15,
    "totalPages": 2
  }
}
```

### 6.8 执行统计

```
GET /api/scheduled-tasks/{id}/statistics
Authorization: Bearer {token}

响应:
{
  "code": 1,
  "msg": "查询成功",
  "data": {
    "taskId": 1001,
    "taskName": "每日API自动化测试",
    "totalExecutions": 15,
    "successfulExecutions": 14,
    "failedExecutions": 1,
    "skippedExecutions": 0,
    "successRate": 93.33,
    "avgDurationSeconds": 325,
    "minDurationSeconds": 280,
    "maxDurationSeconds": 450,
    "lastSevenDaysStats": [
      {
        "date": "2024-09-15",
        "executionCount": 1,
        "passedCount": 1,
        "failedCount": 0,
        "successRate": 100
      }
    ]
  }
}
```

---

## 7. 触发器支持详解

### 7.1 Cron表达式方式

```
Cron表达式: "0 0 9 * * ?"
含义: 每天上午9:00执行

常见Cron表达式:
- "0 0 9 * * ?" - 每天上午9点
- "0 30 9 * * ?" - 每天上午9点30分
- "0 0 */2 * * ?" - 每两小时执行一次
- "0 0 9 ? * MON" - 每周一早上9点
- "0 0 9 1 * ?" - 每月1号早上9点
- "0 0 0 * * ?" - 每天午夜12点
- "*/15 * * * * ?" - 每15秒执行一次
```

### 7.2 简单重复方式 (Simple)

```
重复间隔: 3600000 (毫秒)
重复次数: 10
含义: 每3600秒（1小时）执行一次，共执行10次
```

### 7.3 每日方式 (Daily)

```
小时: 9, 分钟: 0
含义: 每天的9:00执行
```

### 7.4 每周方式 (Weekly)

```
weeklyDays: "1,3,5" (1=周一, 2=周二, 等等)
小时: 9, 分钟: 0
含义: 每周一、三、五的9:00执行
```

### 7.5 每月方式 (Monthly)

```
monthlyDay: 15, 小时: 9, 分钟: 0
含义: 每月15号的9:00执行
```

---

## 8. 执行流程详解

### 8.1 任务创建流程

```
1. 用户提交创建请求 → Controller
2. 参数校验和权限检查 → Service
3. 保存任务到数据库 (ScheduledTestTasks)
4. 为任务创建Quartz Job和Trigger
5. 计算下次触发时间 (nextTriggerTime)
6. 返回任务信息给用户
7. 当触发时间到达时，Quartz自动触发执行
```

### 8.2 任务执行流程

```
1. Quartz在指定时间触发Job
2. ScheduledTestTaskJob.execute() 方法被调用
3. 从JobDataMap中获取taskId和userId
4. 调用 scheduledTaskService.executeScheduledTask()
5. 创建 ScheduledTaskExecution 执行记录 (status=running)
6. 根据任务类型调用对应的测试执行方法
   - 测试用例: testExecutionService.executeTestCase()
   - 模块: testExecutionService.executeModule()
   - 项目: testExecutionService.executeProject()
   - 等等
7. 执行完成后，获取执行结果
8. 更新 ScheduledTaskExecution 的状态和统计信息
9. 创建 TestExecutionRecords 记录（execution_type=scheduled）
10. 更新 ScheduledTestTask 的执行统计和下次触发时间
11. 如果配置了通知，发送成功/失败通知
12. 任务执行完成
```

### 8.3 失败处理和重试流程

```
1. 任务执行失败 → 记录错误信息
2. 检查 retryEnabled 和 maxRetryAttempts
3. 如果启用重试且重试次数未超限:
   a. 创建新的 ScheduledTaskExecution (isRetry=true)
   b. 设置 originalExecutionId 指向原始执行记录
   c. 等待 retryDelayMs 毫秒后重新执行
   d. 重试最多 maxRetryAttempts 次
4. 如果重试也失败或重试次数已超:
   a. 标记任务为失败 (status=failed)
   b. 记录最终错误信息
   c. 如果 notifyOnFailure=true，发送失败通知
   d. 更新 ScheduledTestTask 的 failedExecutions 计数
```

---

## 9. 权限和安全考虑

### 9.1 权限检查

- 创建任务: 需要 `testcase:manage` 或 `test:schedule` 权限
- 编辑任务: 只有创建人或管理员可以编辑
- 删除任务: 需要管理员权限或是创建人
- 执行任务: 需要相应范围的执行权限
- 查看执行历史: 需要 `testcase:view` 权限

### 9.2 安全措施

- 所有API都需要JWT认证
- SQL参数化防止SQL注入
- 输入参数验证
- 操作审计日志
- 任务执行隔离

---

## 10. 监控和告警

### 10.1 执行监控

- 实时查看任务执行状态
- 执行时长预警（超过 maxDurationSeconds）
- 失败率监控

### 10.2 告警机制

- 执行失败邮件通知
- Webhook回调
- 业务指标告警

### 10.3 日志记录

- 完整的执行日志
- 错误堆栈跟踪
- 性能指标记录

---

## 11. 扩展功能建议

### 11.1 短期扩展

1. **任务依赖**: 支持任务之间的依赖关系
2. **条件执行**: 基于前一个任务的结果条件执行
3. **任务分组**: 支持将相关任务分组管理
4. **执行报告**: 自动生成并发送执行报告

### 11.2 长期扩展

1. **分布式调度**: 支持多实例Quartz集群
2. **任务模板**: 预定义常用任务模板
3. **高级告警**: 集成企业级告警系统（钉钉、企业微信等）
4. **数据分析**: 基于历史执行数据的趋势分析
5. **机器学习**: 自动优化调度时间和并发度

---

## 12. 故障排查

### 12.1 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|--------|
| Quartz表不存在 | 未执行初始化脚本 | 执行 quartz_tables.sql |
| 任务不执行 | Quartz未启用或任务被禁用 | 检查配置和任务状态 |
| 重复执行 | 集群环境下的并发问题 | 启用 isClustered 配置 |
| 执行超时 | 任务耗时过长 | 增加 timeoutSeconds 或优化测试 |

---

## 13. 部署检查清单

- [ ] 执行 Quartz 初始化脚本创建必要的数据库表
- [ ] 执行 SQL 脚本创建 ScheduledTestTasks 和 ScheduledTaskExecutions 表
- [ ] 在 application.yml 中启用 Quartz 配置
- [ ] 实现 ScheduledTaskService 和相关 Service
- [ ] 实现 ScheduledTaskController 和所有 API 端点
- [ ] 配置权限和认证
- [ ] 编写单元测试
- [ ] 配置日志记录
- [ ] 测试任务创建、执行、重试、通知等功能
- [ ] 配置监控告警
- [ ] 性能测试（并发任务执行）
- [ ] 文档和培训

---

## 14. 总结

本方案提供了一套完整的定时测试任务解决方案，包括：

1. **灵活的调度机制**: 支持多种触发方式（Cron、Daily、Weekly、Monthly等）
2. **完整的执行管理**: 从创建、执行、监控到记录的全流程
3. **可靠的失败处理**: 支持重试机制和错误通知
4. **详细的执行追踪**: 记录每次执行的完整信息和统计数据
5. **权限和安全**: 完善的权限控制和操作审计

通过本方案，用户可以轻松制定定期自动执行测试的计划，实现测试自动化的最终目标。

