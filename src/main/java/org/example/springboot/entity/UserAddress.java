package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户收货地址实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_address")
@Schema(description = "用户收货地址")
public class UserAddress {

    @TableId(type = IdType.AUTO)
    @Schema(description = "地址ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "收货人姓名")
    private String receiver;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区/县")
    private String district;

    @Schema(description = "详细地址")
    private String detail;

    @Schema(description = "是否为默认地址(0:否 1:是)")
    private Integer isDefault;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 获取完整地址
     */
    public String getFullAddress() {
        return province + city + district + detail;
    }
}

