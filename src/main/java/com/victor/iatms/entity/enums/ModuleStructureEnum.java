package com.victor.iatms.entity.enums;

/**
 * 模块结构类型枚举
 */
public enum ModuleStructureEnum {
    
    /**
     * 树形结构
     */
    TREE("tree", "树形结构"),
    
    /**
     * 平铺结构
     */
    FLAT("flat", "平铺结构");
    
    private final String code;
    private final String desc;
    
    ModuleStructureEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static ModuleStructureEnum getByCode(String code) {
        for (ModuleStructureEnum structure : values()) {
            if (structure.getCode().equals(code)) {
                return structure;
            }
        }
        return null;
    }
}
