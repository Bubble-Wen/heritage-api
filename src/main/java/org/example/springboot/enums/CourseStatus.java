package org.example.springboot.enums;

import lombok.Getter;

/**
 * 课程状态枚举
 * @author system
 */
@Getter
public enum CourseStatus {
    
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    OFFLINE(2, "下架");

    private final Integer code;
    private final String description;

    CourseStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static CourseStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CourseStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 校验状态转换是否合法
     * @param fromStatus 原状态
     * @param toStatus 目标状态
     * @return 是否合法
     */
    public static boolean isValidTransition(Integer fromStatus, Integer toStatus) {
        if (fromStatus == null || toStatus == null) {
            return false;
        }
        
        CourseStatus from = getByCode(fromStatus);
        CourseStatus to = getByCode(toStatus);
        
        if (from == null || to == null) {
            return false;
        }

        // 定义合法的状态转换规则
        switch (from) {
            case DRAFT:
                // 草稿可以转为已发布
                return to == PUBLISHED;
            case PUBLISHED:
                // 已发布可以转为下架或草稿
                return to == OFFLINE || to == DRAFT;
            case OFFLINE:
                // 下架可以转为已发布
                return to == PUBLISHED;
            default:
                return false;
        }
    }

    /**
     * 是否可以编辑
     */
    public boolean canEdit() {
        return this == DRAFT;
    }

    /**
     * 是否可以学习
     */
    public boolean canLearn() {
        return this == PUBLISHED;
    }
}


