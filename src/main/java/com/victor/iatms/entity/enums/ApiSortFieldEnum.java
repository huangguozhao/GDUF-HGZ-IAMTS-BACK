package com.victor.iatms.entity.enums;

/**
 * 接口排序字段枚举
 */
public enum ApiSortFieldEnum {

    NAME("name", "接口名称"),
    METHOD("method", "请求方法"),
    PATH("path", "接口路径"),
    CREATED_AT("created_at", "创建时间"),
    UPDATED_AT("updated_at", "更新时间");

    private final String field;
    private final String desc;

    ApiSortFieldEnum(String field, String desc) {
        this.field = field;
        this.desc = desc;
    }

    public String getField() {
        return field;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isValidSortField(String field) {
        for (ApiSortFieldEnum sortField : values()) {
            if (sortField.getField().equalsIgnoreCase(field)) {
                return true;
            }
        }
        return false;
    }
}
