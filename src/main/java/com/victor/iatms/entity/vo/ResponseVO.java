package com.victor.iatms.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一响应结果封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> {
    
    /**
     * 业务状态码：1代表成功，0代表业务逻辑失败，其他负数代码代表特定错误
     */
    private Integer code;
    
    /**
     * 对当前状态的详细描述信息
     */
    private String msg;
    
    /**
     * 接口返回的有效负载数据
     */
    private T data;
    
    /**
     * 成功响应
     * @param data 数据
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<>(1, "success", data);
    }
    
    /**
     * 成功响应（带消息）
     * @param msg 消息
     * @param data 数据
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ResponseVO<T> success(String msg, T data) {
        return new ResponseVO<>(1, msg, data);
    }
    
    /**
     * 业务逻辑失败
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ResponseVO<T> businessError(String msg) {
        return new ResponseVO<>(0, msg, null);
    }
    
    /**
     * 认证失败
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ResponseVO<T> authError(String msg) {
        return new ResponseVO<>(-1, msg, null);
    }
    
    /**
     * 权限不足
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ResponseVO<T> forbidden(String msg) {
        return new ResponseVO<>(-2, msg, null);
    }
    
    /**
     * 参数校验失败
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ResponseVO<T> paramError(String msg) {
        return new ResponseVO<>(-3, msg, null);
    }
    
    /**
     * 资源不存在
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ResponseVO<T> notFound(String msg) {
        return new ResponseVO<>(-4, msg, null);
    }
    
    /**
     * 服务器内部异常
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ResponseVO<T> serverError(String msg) {
        return new ResponseVO<>(-5, msg, null);
    }


}
