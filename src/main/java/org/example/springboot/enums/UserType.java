package org.example.springboot.enums;

import lombok.Getter;

/**
 * 用户类型枚举
 * @author system
 */
@Getter
public enum UserType {
    
    USER("USER", "普通用户"),
    ADMIN("ADMIN", "管理员");

    private final String code;
    private final String description;

    UserType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static UserType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (UserType type : UserType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的用户类型代码: " + code);
    }

    /**
     * 验证用户类型代码是否有效
     */
    public static boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }
        for (UserType type : UserType.values()) {
            if (type.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
