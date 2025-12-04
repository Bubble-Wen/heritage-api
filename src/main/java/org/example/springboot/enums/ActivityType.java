package org.example.springboot.enums;

import lombok.Getter;

/**
 * 活动类型枚举
 * @author system
 */
@Getter
public enum ActivityType {
    
    PERFORMANCE("展演", "performance"),
    EXHIBITION("展览", "exhibition"),
    TRAINING("培训", "training"),
    COMPETITION("比赛", "competition");

    private final String description;
    private final String code;

    ActivityType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    /**
     * 根据code获取枚举
     */
    public static ActivityType getByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (ActivityType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据描述获取枚举
     */
    public static ActivityType getByDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return null;
        }
        for (ActivityType type : values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        return null;
    }
}


