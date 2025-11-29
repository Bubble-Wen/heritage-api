package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品更新命令DTO
 * @author system
 */
@Data
@Schema(description = "商品更新命令")
public class ShopProductUpdateCommandDTO {

    @Schema(description = "商品标题")
    @NotBlank(message = "商品标题不能为空")
    @Size(max = 200, message = "商品标题长度不能超过200个字符")
    private String title;

    @Schema(description = "副标题")
    @Size(max = 255, message = "副标题长度不能超过255个字符")
    private String subtitle;

    @Schema(description = "类目ID")
    @NotNull(message = "类目ID不能为空")
    private Long categoryId;

    @Schema(description = "商品价格")
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.00", message = "商品价格不能小于0")
    private BigDecimal price;

    @Schema(description = "库存数量")
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能小于0")
    private Integer stock;

    @Schema(description = "商品详情")
    private String detail;

    @Schema(description = "状态 0下架 1上架", example = "1")
    private Integer status;
}

