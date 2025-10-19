# Redis操作重构总结

## 重构内容

### 1. 使用封装的RedisUtils
- 替换了直接使用`RedisTemplate`的方式
- 使用项目中已有的`RedisUtils<String>`工具类
- 提供了更好的错误处理和日志记录

### 2. 创建专门的PasswordResetRedisUtils
- 封装了密码重置相关的所有Redis操作
- 提供了更清晰的业务语义
- 统一管理Redis key前缀和过期时间

## 重构前后对比

### 重构前
```java
@Autowired
private RedisTemplate<String, Object> redisTemplate;

// 直接使用RedisTemplate
redisTemplate.opsForValue().set(codeKey, verificationCode, CODE_EXPIRE_TIME, TimeUnit.SECONDS);
redisTemplate.hasKey(frequencyKey);
redisTemplate.delete(codeKey);
```

### 重构后
```java
@Autowired
private PasswordResetRedisUtils passwordResetRedisUtils;

// 使用封装的工具类
passwordResetRedisUtils.storeVerificationCode(resetTokenId, verificationCode);
passwordResetRedisUtils.checkFrequencyLimit(request.getAccount());
passwordResetRedisUtils.cleanupResetData(resetTokenId, request.getAccount());
```

## 新增的PasswordResetRedisUtils类

### 主要方法
- `checkFrequencyLimit(String account)` - 检查发送频率限制
- `setFrequencyLimit(String account)` - 设置发送频率限制
- `storeVerificationCode(String resetTokenId, String verificationCode)` - 存储验证码
- `getVerificationCode(String resetTokenId)` - 获取验证码
- `deleteVerificationCode(String resetTokenId)` - 删除验证码
- `deleteFrequencyLimit(String account)` - 删除频率限制
- `cleanupResetData(String resetTokenId, String account)` - 清理所有重置数据

### 优势
1. **更好的封装**: 将Redis操作细节隐藏在业务逻辑之外
2. **统一的配置**: 所有Redis key前缀和过期时间集中管理
3. **更好的可维护性**: 如果需要修改Redis操作逻辑，只需要修改工具类
4. **更清晰的业务语义**: 方法名直接表达业务意图
5. **更好的错误处理**: 利用RedisUtils的异常处理机制

## Redis Key设计

### Key前缀
- `password_reset_code:` - 验证码存储
- `password_reset_frequency:` - 频率限制

### 过期时间
- 验证码有效期: 15分钟 (900秒)
- 频率限制: 1分钟 (60秒)

## 使用示例

```java
// 检查频率限制
if (!passwordResetRedisUtils.checkFrequencyLimit(account)) {
    throw new RuntimeException("请求过于频繁，请稍后再试");
}

// 存储验证码
passwordResetRedisUtils.storeVerificationCode(resetTokenId, verificationCode);

// 设置频率限制
passwordResetRedisUtils.setFrequencyLimit(account);

// 清理数据
passwordResetRedisUtils.cleanupResetData(resetTokenId, account);
```

## 技术栈

- Spring Boot 3.x
- Spring Data Redis
- 自定义RedisUtils工具类
- 业务专用的Redis工具类封装
