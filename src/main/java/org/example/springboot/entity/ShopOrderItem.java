package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单明细实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("shop_order_item")
@Schema(description = "订单明细实体类")
public class ShopOrderItem {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    @TableField("order_id")
    private Long orderId;

    @Schema(description = "商品ID(SPU)")
    @NotBlank(message = "商品ID不能为空")
    @Size(max = 50, message = "商品ID长度不能超过50个字符")
    @TableField("product_id")
    private String productId;

    @Schema(description = "SKU ID")
    @NotBlank(message = "SKU ID不能为空")
    @Size(max = 50, message = "SKU ID长度不能超过50个字符")
    @TableField("sku_id")
    private String skuId;

    @Schema(description = "商品标题")
    @NotBlank(message = "商品标题不能为空")
    @Size(max = 200, message = "商品标题长度不能超过200个字符")
    private String title;

    @Schema(description = "SKU标题")
    @Size(max = 200, message = "SKU标题长度不能超过200个字符")
    @TableField("sku_title")
    private String skuTitle;

    @Schema(description = "单价")
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.00", message = "单价不能小于0")
    private BigDecimal price;

    @Schema(description = "数量")
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量不能小于1")
    private Integer quantity;

    @Schema(description = "小计")
    @NotNull(message = "小计不能为空")
    @DecimalMin(value = "0.00", message = "小计不能小于0")
    private BigDecimal subtotal;

    /**
     * 计算小计
     */
    public void calculateSubtotal() {
        if (this.price != null && this.quantity != null) {
            this.subtotal = this.price.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    /**
     * 获取完整商品名称
     */
    public String getFullTitle() {
        if (skuTitle != null && !skuTitle.trim().isEmpty()) {
            return title + " - " + skuTitle;
        }
        return title;
    }
}

