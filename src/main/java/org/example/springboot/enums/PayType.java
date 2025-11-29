package org.example.springboot.enums;

/**
 * 支付方式枚举
 * @author system
 */
public enum PayType {
    
    /**
     * 支付宝
     */
    ALIPAY("ALI", "支付宝"),
    
    /**
     * 微信支付
     */
    WECHAT("WECHAT", "微信支付"),
    
    /**
     * 其他
     */
    OTHER("OTHER", "其他");

    private final String code;
    private final String description;

    PayType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     */
    public static PayType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (PayType type : PayType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 获取描述
     */
    public static String getDescription(String code) {
        PayType type = fromCode(code);
        return type != null ? type.description : "未知";
    }
}

