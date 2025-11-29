package org.example.springboot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 非遗作品状态枚举
 * @author system
 */
@Getter
@AllArgsConstructor
public enum HeritageItemStatus {
    
    DRAFT(0, "草稿"),
    PENDING(1, "待审"),
    PUBLISHED(2, "已发布"),
    OFFLINE(3, "下架");

    private final Integer code;
    private final String description;

    /**
     * 根据状态码获取枚举
     */
    public static HeritageItemStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (HeritageItemStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 验证状态码是否有效
     */
    public static boolean isValidCode(Integer code) {
        return fromCode(code) != null;
    }

    /**
     * 是否为草稿状态
     */
    public boolean isDraft() {
        return this == DRAFT;
    }

    /**
     * 是否为待审状态
     */
    public boolean isPending() {
        return this == PENDING;
    }

    /**
     * 是否为已发布状态
     */
    public boolean isPublished() {
        return this == PUBLISHED;
    }

    /**
     * 是否为下架状态
     */
    public boolean isOffline() {
        return this == OFFLINE;
    }

    /**
     * 是否可以编辑
     */
    public boolean canEdit() {
        return this == DRAFT || this == PENDING;
    }

    /**
     * 是否可以删除
     */
    public boolean canDelete() {
        return this == DRAFT || this == OFFLINE;
    }

    /**
     * 是否可以发布
     */
    public boolean canPublish() {
        return this == DRAFT || this == PENDING;
    }

    /**
     * 是否可以下架
     */
    public boolean canOffline() {
        return this == PUBLISHED;
    }
}

