package com.victor.iatms.entity.constants;

public class Constants {
    public static final String ZERO_STR = "0";

    public static final Integer ZERO = 0;

    public static final Integer ONE = 1;

    public static final Integer LENGTH_10 = 10;
    public static final Integer LENGTH_11 = 11;
    public static final Integer LENGTH_20 = 20;

    public static final Integer LENGTH_30 = 30;

    public static final String IMAGE_SUFFIX = ".png";

    public static final String COVER_IMAGE_SUFFIX = "_cover.png";

    public static final String[] IMAGE_SUFFIX_LIST = new String[]{".jpeg", ".jpg", ".png", ".gif", ".bmp", ".webp"};

    public static final String[] VIDEO_SUFFIX_LIST = new String[]{".mp4", ".avi", ".rmvb", ".mkv", ".mov"};

    public static final Long FILE_SIZE_MB = 1024 * 1024L;

    // Redis key前缀
    public static final String RESET_CODE_PREFIX = "password_reset_code:";
    public static final String RESET_FREQUENCY_PREFIX = "password_reset_frequency:";

    // 验证码有效期（15分钟）
    public static final long CODE_EXPIRE_TIME = 15 * 60;

    // 发送频率限制（1分钟内只能发送一次）
    public static final long FREQUENCY_LIMIT_TIME = 60;


    /**
     * redis key 相关
     */

    /**
     * 过期时间 1分钟
     */
    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60;


    public static final Integer REDIS_KEY_EXPIRES_HEART_BEAT = 6;

    /**
     * 过期时间 1天
     */
    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;


    public static final Integer REDIS_KEY_TOKEN_EXPIRES = REDIS_KEY_EXPIRES_DAY * 2;


    public static final Long MILLISECOND_3DAYS_AGO = 3 * 24 * 60 * 60 * 1000L;

    /**
     * 测试用例管理相关常量
     */
    
    /**
     * 分页相关常量
     */
    public static final Integer DEFAULT_PAGE = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer MAX_PAGE_SIZE = 100;
    
    /**
     * 测试用例状态
     */
    public static final String TEST_CASE_ENABLED = "true";
    public static final String TEST_CASE_DISABLED = "false";
    public static final String TEST_CASE_TEMPLATE = "true";
    public static final String TEST_CASE_NORMAL = "false";
    
    /**
     * 接口状态
     */
    public static final String API_STATUS_ACTIVE = "active";
    public static final String API_STATUS_INACTIVE = "inactive";
    public static final String API_STATUS_DEPRECATED = "deprecated";
    
    /**
     * 默认版本号
     */
    public static final String DEFAULT_VERSION = "1.0";
    
    /**
     * 默认超时时间（秒）
     */
    public static final Integer DEFAULT_TIMEOUT_SECONDS = 30;
    
    /**
     * 测试用例相关常量
     */
    
    /**
     * 测试用例编码前缀
     */
    public static final String TEST_CASE_CODE_PREFIX = "TC-API-";
    
    /**
     * 测试用例编码分隔符
     */
    public static final String TEST_CASE_CODE_SEPARATOR = "-";
    
    /**
     * 测试用例编码最大长度
     */
    public static final Integer TEST_CASE_CODE_MAX_LENGTH = 50;
    
    /**
     * 测试用例名称最大长度
     */
    public static final Integer TEST_CASE_NAME_MAX_LENGTH = 255;
    
    /**
     * 测试用例描述最大长度
     */
    public static final Integer TEST_CASE_DESCRIPTION_MAX_LENGTH = 1000;
    
    /**
     * 默认优先级
     */
    public static final String DEFAULT_PRIORITY = "P2";
    
    /**
     * 默认严重程度
     */
    public static final String DEFAULT_SEVERITY = "medium";
    
    /**
     * 默认启用状态
     */
    public static final Boolean DEFAULT_ENABLED = true;
    
    /**
     * 默认模板状态
     */
    public static final Boolean DEFAULT_TEMPLATE = false;

    /**
     * 文件导入相关常量
     */
    
    /**
     * 支持的文件格式
     */
    public static final String[] SUPPORTED_FILE_EXTENSIONS = {".xlsx", ".xls", ".csv"};
    
    /**
     * 最大文件大小（10MB）
     */
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * 导入模式
     */
    public static final String IMPORT_MODE_INSERT = "insert";
    public static final String IMPORT_MODE_UPSERT = "upsert";
    
    /**
     * 冲突处理策略
     */
    public static final String CONFLICT_STRATEGY_SKIP = "skip";
    public static final String CONFLICT_STRATEGY_OVERWRITE = "overwrite";
    public static final String CONFLICT_STRATEGY_RENAME = "rename";
    
    /**
     * 模板类型
     */
    public static final String TEMPLATE_TYPE_SIMPLE = "simple";
    public static final String TEMPLATE_TYPE_STANDARD = "standard";
    public static final String TEMPLATE_TYPE_ADVANCED = "advanced";

    /**
     * 文件导出相关常量
     */
    
    /**
     * 支持的导出格式
     */
    public static final String EXPORT_FORMAT_EXCEL = "excel";
    public static final String EXPORT_FORMAT_CSV = "csv";
    
    /**
     * 默认导出字段
     */
    public static final String[] DEFAULT_EXPORT_FIELDS = {
        "case_code", "name", "description", "priority", "severity", 
        "tags", "expected_http_status", "is_enabled", "created_at"
    };
    
    /**
     * 所有可导出的字段
     */
    public static final String[] ALL_EXPORT_FIELDS = {
        "case_code", "name", "description", "priority", "severity", 
        "tags", "pre_conditions", "test_steps", "request_override",
        "expected_http_status", "expected_response_schema", "expected_response_body",
        "assertions", "extractors", "validators", "is_enabled", "is_template",
        "version", "created_by", "creator_name", "created_at", "updated_at"
    };
    
    /**
     * Excel文件MIME类型
     */
    public static final String EXCEL_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    
    /**
     * CSV文件MIME类型
     */
    public static final String CSV_MIME_TYPE = "text/csv";

    /**
     * 测试执行相关常量
     */
    
    /**
     * 默认执行超时时间（秒）
     */
    public static final int DEFAULT_EXECUTION_TIMEOUT = 30;
    
    /**
     * 最大执行超时时间（秒）
     */
    public static final int MAX_EXECUTION_TIMEOUT = 300;
    
    /**
     * 默认环境配置
     */
    public static final String DEFAULT_ENVIRONMENT = "test";
    
    /**
     * 支持的环境列表
     */
    public static final String[] SUPPORTED_ENVIRONMENTS = {"dev", "test", "prod", "staging"};
    
    /**
     * 异步执行队列名称
     */
    public static final String ASYNC_EXECUTION_QUEUE = "test_execution_queue";
    
    /**
     * 任务状态检查间隔（秒）
     */
    public static final int TASK_STATUS_CHECK_INTERVAL = 5;
    
    /**
     * 任务最大等待时间（秒）
     */
    public static final int MAX_TASK_WAIT_TIME = 3600;
    
    /**
     * 报告生成超时时间（秒）
     */
    public static final int REPORT_GENERATION_TIMEOUT = 300;
    
    /**
     * 默认报告格式
     */
    public static final String DEFAULT_REPORT_FORMAT = "html";
    
    /**
     * 报告文件存储路径
     */
    public static final String REPORT_STORAGE_PATH = "/reports/";
    
    /**
     * 日志文件存储路径
     */
    public static final String LOG_STORAGE_PATH = "/logs/";
    
    /**
     * 截图文件存储路径
     */
    public static final String SCREENSHOT_STORAGE_PATH = "/screenshots/";
    
    /**
     * 视频文件存储路径
     */
    public static final String VIDEO_STORAGE_PATH = "/videos/";

    /**
     * 模块执行相关常量
     */
    
    /**
     * 默认并发执行数
     */
    public static final int DEFAULT_CONCURRENCY = 5;
    
    /**
     * 最大并发执行数
     */
    public static final int MAX_CONCURRENCY = 50;
    
    /**
     * 最小并发执行数
     */
    public static final int MIN_CONCURRENCY = 1;
    
    /**
     * 默认异步执行
     */
    public static final boolean DEFAULT_ASYNC_EXECUTION = true;
    
    /**
     * 模块执行任务前缀
     */
    public static final String MODULE_TASK_PREFIX = "module_task_";
    
    /**
     * 模块执行状态检查间隔（秒）
     */
    public static final int MODULE_STATUS_CHECK_INTERVAL = 3;
    
    /**
     * 模块执行最大等待时间（秒）
     */
    public static final int MODULE_MAX_WAIT_TIME = 7200; // 2小时
    
    /**
     * 模块执行预估时间基数（每个用例预估2秒）
     */
    public static final int ESTIMATED_TIME_PER_CASE = 2;
    
    /**
     * 模块执行队列名称
     */
    public static final String MODULE_EXECUTION_QUEUE = "module_execution_queue";
    
    /**
     * 模块执行结果缓存时间（小时）
     */
    public static final int MODULE_RESULT_CACHE_HOURS = 24;

    // ========== 项目执行相关常量 ==========
    
    /**
     * 项目执行任务前缀
     */
    public static final String PROJECT_TASK_PREFIX = "project_task_";
    
    /**
     * 项目执行状态检查间隔（秒）
     */
    public static final int PROJECT_STATUS_CHECK_INTERVAL = 5;
    
    /**
     * 项目执行最大等待时间（秒）
     */
    public static final int PROJECT_MAX_WAIT_TIME = 14400; // 4小时
    
    /**
     * 项目执行预估时间基数（每个用例预估3秒）
     */
    public static final int PROJECT_ESTIMATED_TIME_PER_CASE = 3;
    
    /**
     * 项目执行队列名称
     */
    public static final String PROJECT_EXECUTION_QUEUE = "project_execution_queue";
    
    /**
     * 项目执行结果缓存时间（小时）
     */
    public static final int PROJECT_RESULT_CACHE_HOURS = 48;
    
    /**
     * 项目默认并发执行数
     */
    public static final int PROJECT_DEFAULT_CONCURRENCY = 10;
    
    /**
     * 项目最大并发执行数
     */
    public static final int PROJECT_MAX_CONCURRENCY = 50;
    
    /**
     * 项目最小并发执行数
     */
    public static final int PROJECT_MIN_CONCURRENCY = 1;
    
    /**
     * 执行策略常量
     */
    public static final String EXECUTION_STRATEGY_SEQUENTIAL = "sequential";
    public static final String EXECUTION_STRATEGY_BY_MODULE = "by_module";
    public static final String EXECUTION_STRATEGY_BY_PRIORITY = "by_priority";
    
    /**
     * 默认执行策略
     */
    public static final String DEFAULT_EXECUTION_STRATEGY = EXECUTION_STRATEGY_BY_MODULE;

    // ========== 接口执行相关常量 ==========
    
    /**
     * 接口执行任务前缀
     */
    public static final String API_TASK_PREFIX = "api_task_";
    
    /**
     * 接口执行状态检查间隔（秒）
     */
    public static final int API_STATUS_CHECK_INTERVAL = 3;
    
    /**
     * 接口执行最大等待时间（秒）
     */
    public static final int API_MAX_WAIT_TIME = 1800; // 30分钟
    
    /**
     * 接口执行预估时间基数（每个用例预估5秒）
     */
    public static final int API_ESTIMATED_TIME_PER_CASE = 5;
    
    /**
     * 接口执行队列名称
     */
    public static final String API_EXECUTION_QUEUE = "api_execution_queue";
    
    /**
     * 接口执行结果缓存时间（小时）
     */
    public static final int API_RESULT_CACHE_HOURS = 12;
    
    /**
     * 接口默认并发执行数
     */
    public static final int API_DEFAULT_CONCURRENCY = 3;
    
    /**
     * 接口最大并发执行数
     */
    public static final int API_MAX_CONCURRENCY = 10;
    
    /**
     * 接口最小并发执行数
     */
    public static final int API_MIN_CONCURRENCY = 1;
    
    /**
     * 执行顺序常量
     */
    public static final String EXECUTION_ORDER_PRIORITY_DESC = "priority_desc";
    public static final String EXECUTION_ORDER_PRIORITY_ASC = "priority_asc";
    public static final String EXECUTION_ORDER_NAME_ASC = "name_asc";
    public static final String EXECUTION_ORDER_NAME_DESC = "name_desc";
    
    /**
     * 默认执行顺序
     */
    public static final String DEFAULT_EXECUTION_ORDER = EXECUTION_ORDER_PRIORITY_DESC;

    // ========== 测试套件执行相关常量 ==========
    
    /**
     * 测试套件执行任务前缀
     */
    public static final String SUITE_TASK_PREFIX = "suite_task_";
    
    /**
     * 测试套件执行状态检查间隔（秒）
     */
    public static final int SUITE_STATUS_CHECK_INTERVAL = 5;
    
    /**
     * 测试套件执行最大等待时间（秒）
     */
    public static final int SUITE_MAX_WAIT_TIME = 7200; // 2小时
    
    /**
     * 测试套件执行预估时间基数（每个用例预估7秒）
     */
    public static final int SUITE_ESTIMATED_TIME_PER_CASE = 7;
    
    /**
     * 测试套件执行队列名称
     */
    public static final String SUITE_EXECUTION_QUEUE = "suite_execution_queue";
    
    /**
     * 测试套件执行结果缓存时间（小时）
     */
    public static final int SUITE_RESULT_CACHE_HOURS = 24;
    
    /**
     * 测试套件默认并发执行数
     */
    public static final int SUITE_DEFAULT_CONCURRENCY = 8;
    
    /**
     * 测试套件最大并发执行数
     */
    public static final int SUITE_MAX_CONCURRENCY = 20;
    
    /**
     * 测试套件最小并发执行数
     */
    public static final int SUITE_MIN_CONCURRENCY = 1;
    
    /**
     * 执行策略常量
     */
    public static final String EXECUTION_STRATEGY_PARALLEL = "parallel";
    public static final String EXECUTION_STRATEGY_SMART = "smart";
    
    /**
     * 默认测试套件执行策略
     */
    public static final String DEFAULT_SUITE_EXECUTION_STRATEGY = EXECUTION_STRATEGY_SMART;
    
    /**
     * 重试配置常量
     */
    public static final int DEFAULT_MAX_RETRY_ATTEMPTS = 3;
    public static final int DEFAULT_RETRY_DELAY_MS = 1000;
    public static final int MAX_RETRY_ATTEMPTS = 5;
    public static final int MAX_RETRY_DELAY_MS = 10000;

    // ========== 项目管理相关常量 ==========
    
    /**
     * 项目状态
     */
    public static final String PROJECT_STATUS_ACTIVE = "active";
    public static final String PROJECT_STATUS_INACTIVE = "inactive";
    public static final String PROJECT_STATUS_ARCHIVED = "archived";
    
    /**
     * 项目编码前缀
     */
    public static final String PROJECT_CODE_PREFIX = "PROJ-";
    
    /**
     * 项目编码分隔符
     */
    public static final String PROJECT_CODE_SEPARATOR = "-";
    
    /**
     * 项目编码最大长度
     */
    public static final Integer PROJECT_CODE_MAX_LENGTH = 50;
    
    /**
     * 项目名称最大长度
     */
    public static final Integer PROJECT_NAME_MAX_LENGTH = 255;
    
    /**
     * 项目描述最大长度
     */
    public static final Integer PROJECT_DESCRIPTION_MAX_LENGTH = 1000;
    
    /**
     * 默认项目状态
     */
    public static final String DEFAULT_PROJECT_STATUS = PROJECT_STATUS_ACTIVE;
    
    /**
     * 项目列表默认排序字段
     */
    public static final String DEFAULT_PROJECT_SORT_BY = "created_at";
    
    /**
     * 项目列表默认排序顺序
     */
    public static final String DEFAULT_PROJECT_SORT_ORDER = "desc";
    
    /**
     * 项目权限相关常量
     */
    public static final String PROJECT_PERMISSION_VIEW = "view";
    public static final String PROJECT_PERMISSION_EDIT = "edit";
    public static final String PROJECT_PERMISSION_DELETE = "delete";
    public static final String PROJECT_PERMISSION_ADMIN = "admin";
    
    /**
     * 项目删除相关常量
     */
    
    /**
     * 系统项目标识
     */
    public static final String SYSTEM_PROJECT_NAME_KEYWORD = "系统";
    public static final String SYSTEM_PROJECT_CODE_PREFIX = "SYS";
    
    /**
     * 删除错误代码
     */
    public static final String DELETE_ERROR_PROJECT_NOT_FOUND = "PROJECT_NOT_FOUND";
    public static final String DELETE_ERROR_PROJECT_ALREADY_DELETED = "PROJECT_ALREADY_DELETED";
    public static final String DELETE_ERROR_CANNOT_DELETE_SYSTEM_PROJECT = "CANNOT_DELETE_SYSTEM_PROJECT";
    public static final String DELETE_ERROR_HAS_RELATED_DATA = "HAS_RELATED_DATA";
    public static final String DELETE_ERROR_PERMISSION_DENIED = "PERMISSION_DENIED";
    public static final String DELETE_ERROR_PARAM_ERROR = "PARAM_ERROR";
    public static final String DELETE_ERROR_DELETE_FAILED = "DELETE_FAILED";
    public static final String DELETE_ERROR_SYSTEM_ERROR = "SYSTEM_ERROR";
    
    /**
     * 级联删除类型
     */
    public static final String CASCADE_DELETE_TYPE_MODULES = "modules";
    public static final String CASCADE_DELETE_TYPE_APIS = "apis";
    public static final String CASCADE_DELETE_TYPE_TEST_CASES = "testCases";
    public static final String CASCADE_DELETE_TYPE_TEST_REPORTS = "testReports";
    
    /**
     * 默认强制删除
     */
    public static final Boolean DEFAULT_FORCE_DELETE = false;
    
    /**
     * 项目编辑相关常量
     */
    
    /**
     * 编辑错误代码
     */
    public static final String UPDATE_ERROR_PROJECT_NOT_FOUND = "PROJECT_NOT_FOUND";
    public static final String UPDATE_ERROR_PROJECT_ALREADY_DELETED = "PROJECT_ALREADY_DELETED";
    public static final String UPDATE_ERROR_CANNOT_EDIT_SYSTEM_PROJECT = "CANNOT_EDIT_SYSTEM_PROJECT";
    public static final String UPDATE_ERROR_NAME_ALREADY_EXISTS = "NAME_ALREADY_EXISTS";
    public static final String UPDATE_ERROR_PERMISSION_DENIED = "PERMISSION_DENIED";
    public static final String UPDATE_ERROR_PARAM_ERROR = "PARAM_ERROR";
    public static final String UPDATE_ERROR_UPDATE_FAILED = "UPDATE_FAILED";
    public static final String UPDATE_ERROR_SYSTEM_ERROR = "SYSTEM_ERROR";
    
    /**
     * 编辑操作类型
     */
    public static final String UPDATE_OPERATION_TYPE_NAME = "name";
    public static final String UPDATE_OPERATION_TYPE_DESCRIPTION = "description";
    public static final String UPDATE_OPERATION_TYPE_BOTH = "both";
    
    /**
     * 项目成员相关常量
     */
    
    /**
     * 默认成员状态
     */
    public static final String DEFAULT_MEMBER_STATUS = "active";
    
    /**
     * 默认成员排序字段
     */
    public static final String DEFAULT_MEMBER_SORT_BY = "join_time";
    
    /**
     * 默认排序顺序
     */
    public static final String DEFAULT_SORT_ORDER = "asc";
    
    /**
     * 成员状态枚举
     */
    public static final String MEMBER_STATUS_ACTIVE = "active";
    public static final String MEMBER_STATUS_INACTIVE = "inactive";
    public static final String MEMBER_STATUS_REMOVED = "removed";
    
    /**
     * 权限级别枚举
     */
    public static final String PERMISSION_LEVEL_READ = "read";
    public static final String PERMISSION_LEVEL_WRITE = "write";
    public static final String PERMISSION_LEVEL_ADMIN = "admin";
    
    /**
     * 项目角色枚举
     */
    public static final String PROJECT_ROLE_OWNER = "owner";
    public static final String PROJECT_ROLE_MANAGER = "manager";
    public static final String PROJECT_ROLE_DEVELOPER = "developer";
    public static final String PROJECT_ROLE_TESTER = "tester";
    public static final String PROJECT_ROLE_VIEWER = "viewer";
    
    /**
     * 成员排序字段枚举
     */
    public static final String MEMBER_SORT_BY_JOIN_TIME = "join_time";
    public static final String MEMBER_SORT_BY_NAME = "name";
    public static final String MEMBER_SORT_BY_PERMISSION_LEVEL = "permission_level";
    public static final String MEMBER_SORT_BY_PROJECT_ROLE = "project_role";
    
    /**
     * 模块相关常量
     */
    
    /**
     * 默认模块结构
     */
    public static final String DEFAULT_MODULE_STRUCTURE = "flat";
    
    /**
     * 默认模块状态
     */
    public static final String DEFAULT_MODULE_STATUS = "active";
    
    /**
     * 默认是否包含已删除
     */
    public static final Boolean DEFAULT_INCLUDE_DELETED = false;
    
    /**
     * 默认是否包含统计信息
     */
    public static final Boolean DEFAULT_INCLUDE_STATISTICS = false;
    
    /**
     * 默认模块排序字段
     */
    public static final String DEFAULT_MODULE_SORT_BY = "sort_order";
    
    /**
     * 模块状态枚举
     */
    public static final String MODULE_STATUS_ACTIVE = "active";
    public static final String MODULE_STATUS_INACTIVE = "inactive";
    public static final String MODULE_STATUS_ARCHIVED = "archived";
    
    /**
     * 模块结构类型枚举
     */
    public static final String MODULE_STRUCTURE_TREE = "tree";
    public static final String MODULE_STRUCTURE_FLAT = "flat";
    
    /**
     * 模块排序字段枚举
     */
    public static final String MODULE_SORT_BY_SORT_ORDER = "sort_order";
    public static final String MODULE_SORT_BY_NAME = "name";
    public static final String MODULE_SORT_BY_CREATED_AT = "created_at";

}
