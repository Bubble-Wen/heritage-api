package org.example.springboot.enums;

import lombok.Getter;

/**
 * 活动状态枚举
 * @author system
 */
@Getter
public enum ActivityStatus {
    
    DRAFT(0, "草稿"),
    REGISTERING(1, "报名中"),
    IN_PROGRESS(2, "进行中"),
    FINISHED(3, "已结束");

    private final Integer code;
    private final String description;

    ActivityStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ActivityStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ActivityStatus status : values()) {
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
        
        ActivityStatus from = getByCode(fromStatus);
        ActivityStatus to = getByCode(toStatus);
        
        if (from == null || to == null) {
            return false;
        }

        // 定义合法的状态转换规则
        switch (from) {
            case DRAFT:
                // 草稿可以转为报名中
                return to == REGISTERING;
            case REGISTERING:
                // 报名中可以转为进行中或草稿
                return to == IN_PROGRESS || to == DRAFT;
            case IN_PROGRESS:
                // 进行中可以转为已结束
                return to == FINISHED;
            case FINISHED:
                // 已结束不能再转换
                return false;
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
     * 是否可以报名
     */
    public boolean canSignup() {
        return this == REGISTERING;
    }

    /**
     * 是否可以签到
     */
    public boolean canCheckIn() {
        return this == IN_PROGRESS;
    }
}


