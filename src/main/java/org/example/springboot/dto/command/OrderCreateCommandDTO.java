package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建订单命令DTO
 * @author system
 */
@Data
@Schema(description = "创建订单命令DTO")
public class OrderCreateCommandDTO {

    @Schema(description = "商品ID", example = "SP-2025-001")
    @NotBlank(message = "商品ID不能为空")
    @Size(max = 50, message = "商品ID长度不能超过50个字符")
    private String productId;

    @Schema(description = "购买数量", example = "1")
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity;

    @Schema(description = "收货地址ID", example = "1")
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;

    @Schema(description = "订单备注", example = "请尽快发货")
    @Size(max = 200, message = "订单备注长度不能超过200个字符")
    private String remark;
}

