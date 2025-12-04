package org.example.springboot.enums;

import lombok.Getter;

/**
 * 课程难度等级枚举
 * @author system
 */
@Getter
public enum CourseLevel {
    
    BEGINNER("入门", "beginner", 1),
    ELEMENTARY("初级", "elementary", 2),
    INTERMEDIATE("中级", "intermediate", 3),
    ADVANCED("高级", "advanced", 4);

    private final String description;
    private final String code;
    private final Integer level;

    CourseLevel(String description, String code, Integer level) {
        this.description = description;
        this.code = code;
        this.level = level;
    }

    /**
     * 根据code获取枚举
     */
    public static CourseLevel getByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (CourseLevel level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }

    /**
     * 根据描述获取枚举
     */
    public static CourseLevel getByDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return null;
        }
        for (CourseLevel level : values()) {
            if (level.getDescription().equals(description)) {
                return level;
            }
        }
        return null;
    }

    /**
     * 比较难度等级
     * @param other 另一个等级
     * @return 正数表示本等级更高，负数表示本等级更低，0表示相同
     */
    public int compareLevel(CourseLevel other) {
        if (other == null) {
            return 1;
        }
        return this.level.compareTo(other.getLevel());
    }
}

