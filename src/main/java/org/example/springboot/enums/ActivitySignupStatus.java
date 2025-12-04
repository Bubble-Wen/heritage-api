package org.example.springboot.enums;

import lombok.Getter;

/**
 * 活动报名状态枚举
 * @author system
 */
@Getter
public enum ActivitySignupStatus {
    
    PENDING(0, "待审"),
    APPROVED(1, "通过"),
    REJECTED(2, "拒绝"),
    CHECKED_IN(3, "已签到");

    private final Integer code;
    private final String description;

    ActivitySignupStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ActivitySignupStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ActivitySignupStatus status : values()) {
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
        
        ActivitySignupStatus from = getByCode(fromStatus);
        ActivitySignupStatus to = getByCode(toStatus);
        
        if (from == null || to == null) {
            return false;
        }

        // 定义合法的状态转换规则
        switch (from) {
            case PENDING:
                // 待审可以转为通过或拒绝
                return to == APPROVED || to == REJECTED;
            case APPROVED:
                // 通过可以转为已签到
                return to == CHECKED_IN;
            case REJECTED:
            case CHECKED_IN:
                // 拒绝和已签到不能再转换
                return false;
            default:
                return false;
        }
    }

    /**
     * 是否可以签到
     */
    public boolean canCheckIn() {
        return this == APPROVED;
    }
}


