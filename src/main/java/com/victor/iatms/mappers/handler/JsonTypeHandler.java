package com.victor.iatms.mappers.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis JSON类型处理器
 * 用于将数据库中的JSON字符串转换为Java对象
 */
@MappedTypes({Object.class})
public class JsonTypeHandler extends BaseTypeHandler<Object> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }
    
    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }
    
    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }
    
    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }
    
    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("转换为JSON失败", e);
        }
    }
    
    private Object parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            Object result = objectMapper.readValue(json, Object.class);
            // 如果解析结果是ArrayList，尝试将其中的元素转换为字符串
            if (result instanceof java.util.List) {
                java.util.List<?> list = (java.util.List<?>) result;
                java.util.List<String> stringList = new java.util.ArrayList<>();
                for (Object item : list) {
                    if (item instanceof String) {
                        stringList.add((String) item);
                    } else if (item instanceof java.util.Map) {
                        // 如果是Map对象，尝试提取其中的值
                        java.util.Map<?, ?> map = (java.util.Map<?, ?>) item;
                        if (map.containsKey("name")) {
                            stringList.add(String.valueOf(map.get("name")));
                        } else if (!map.isEmpty()) {
                            // 取第一个值
                            stringList.add(String.valueOf(map.values().iterator().next()));
                        }
                    } else {
                        stringList.add(String.valueOf(item));
                    }
                }
                return stringList;
            }
            return result;
        } catch (Exception e) {
            // 如果解析失败，返回原始字符串
            return json;
        }
    }
}

