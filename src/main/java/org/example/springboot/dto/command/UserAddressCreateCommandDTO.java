package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建收货地址命令DTO
 * @author system
 */
@Data
@Schema(description = "创建收货地址命令DTO")
public class UserAddressCreateCommandDTO {

    @Schema(description = "收货人", example = "张三")
    @NotBlank(message = "收货人不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50个字符")
    private String receiver;

    @Schema(description = "手机号", example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "省", example = "江苏省")
    @NotBlank(message = "省份不能为空")
    @Size(max = 50, message = "省份长度不能超过50个字符")
    private String province;

    @Schema(description = "市", example = "苏州市")
    @NotBlank(message = "城市不能为空")
    @Size(max = 50, message = "城市长度不能超过50个字符")
    private String city;

    @Schema(description = "区/县", example = "姑苏区")
    @NotBlank(message = "区/县不能为空")
    @Size(max = 50, message = "区/县长度不能超过50个字符")
    private String district;

    @Schema(description = "详细地址", example = "观前街100号")
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200个字符")
    private String detail;

    @Schema(description = "是否设为默认地址", example = "true")
    private Boolean isDefault;
}

