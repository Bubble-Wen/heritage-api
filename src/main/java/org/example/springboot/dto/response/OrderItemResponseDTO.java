package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单商品明细响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单商品明细响应DTO")
public class OrderItemResponseDTO {

    @Schema(description = "明细ID")
    private Long id;

    @Schema(description = "商品ID")
    private String productId;

    @Schema(description = "SKU ID")
    private String skuId;

    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "SKU标题")
    private String skuTitle;

    @Schema(description = "完整商品名称")
    private String fullTitle;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "小计")
    private BigDecimal subtotal;
}

