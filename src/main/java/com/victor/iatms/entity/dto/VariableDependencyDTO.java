package com.victor.iatms.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class VariableDependencyDTO {

    private String name;

    private String source;

    private String sourceType;

    private Integer sourceId;

    private String extractPath;

    private String defaultValue;

    private Boolean required;

    private String description;

    @Data
    public static class SourceInfo {
        private Integer id;
        private String type;
        private String name;
        private String path;
    }
}
