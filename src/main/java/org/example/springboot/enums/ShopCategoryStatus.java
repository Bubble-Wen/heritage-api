package org.example.springboot.enums;

import lombok.Getter;

/**
 * 商品分类状态枚举
 * @author system
 */
@Getter
public enum ShopCategoryStatus {
    
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final Integer code;
    private final String description;

    ShopCategoryStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ShopCategoryStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ShopCategoryStatus status : values()) {
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
        
        ShopCategoryStatus from = getByCode(fromStatus);
        ShopCategoryStatus to = getByCode(toStatus);
        
        if (from == null || to == null) {
            return false;
        }

        // 分类状态可以自由切换
        return true;
    }

    /**
     * 是否可以添加子分类
     */
    public boolean canAddChild() {
        return this == ENABLED;
    }

    /**
     * 是否可以关联商品
     */
    public boolean canLinkProduct() {
        return this == ENABLED;
    }
}
