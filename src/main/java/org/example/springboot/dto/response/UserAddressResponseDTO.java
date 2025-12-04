package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收货地址响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收货地址响应DTO")
public class UserAddressResponseDTO {

    @Schema(description = "地址ID")
    private Long id;

    @Schema(description = "收货人")
    private String receiver;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区/县")
    private String district;

    @Schema(description = "详细地址")
    private String detail;

    @Schema(description = "完整地址")
    private String fullAddress;

    @Schema(description = "是否默认地址")
    private Integer isDefault;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

