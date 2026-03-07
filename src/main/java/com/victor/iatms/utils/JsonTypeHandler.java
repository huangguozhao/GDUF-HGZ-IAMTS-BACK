package com.victor.iatms.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON类型处理器
 * 用于处理JSON字段与Java对象的转换
 */
@MappedTypes({Map.class, Object.class})
public class JsonTypeHandler extends BaseTypeHandler<Object> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting object to JSON string", e);
        }
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
    
    private Object parseJson(String json) throws SQLException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            Object result = objectMapper.readValue(json, Object.class);
            // 如果解析结果是ArrayList，尝试将其中的元素转换为字符串
            if (result instanceof List) {
                List<?> list = (List<?>) result;
                List<String> stringList = new ArrayList<>();
                for (Object item : list) {
                    if (item instanceof String) {
                        stringList.add((String) item);
                    } else if (item instanceof Map) {
                        // 如果是Map对象，尝试提取其中的值
                        Map<?, ?> map = (Map<?, ?>) item;
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
        } catch (JsonProcessingException e) {
            throw new SQLException("Error parsing JSON string: " + json, e);
        }
    }
}
