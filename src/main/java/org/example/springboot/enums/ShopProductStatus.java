package org.example.springboot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品状态枚举
 * @author system
 */
@Getter
@AllArgsConstructor
public enum ShopProductStatus {

    /**
     * 下架
     */
    OFF_SHELF(0, "下架"),

    /**
     * 上架
     */
    ON_SHELF(1, "上架");

    private final Integer code;
    private final String description;

    /**
     * 根据code获取枚举
     */
    public static ShopProductStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ShopProductStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据code获取描述
     */
    public static String getDescriptionByCode(Integer code) {
        ShopProductStatus status = fromCode(code);
        return status != null ? status.getDescription() : "未知";
    }
}

