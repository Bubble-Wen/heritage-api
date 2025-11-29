package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
import java.time.LocalDateTime;

/**
 * 商品实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("shop_product")
@Schema(description = "商品实体类")
public class ShopProduct {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "商品ID(支持数字ID和UUID)")
    @Size(max = 50, message = "ID长度不能超过50个字符")
    private String id;

    @Schema(description = "商品标题")
    @NotBlank(message = "商品标题不能为空")
    @Size(max = 200, message = "商品标题长度不能超过200个字符")
    private String title;

    @Schema(description = "副标题")
    @Size(max = 255, message = "副标题长度不能超过255个字符")
    private String subtitle;

    @Schema(description = "类目ID")
    @NotNull(message = "类目ID不能为空")
    @TableField("category_id")
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

    @Schema(description = "状态 0下架 1上架")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否上架
     */
    public boolean isOnSale() {
        return this.status != null && this.status == 1;
    }

    /**
     * 是否下架
     */
    public boolean isOffShelf() {
        return this.status != null && this.status == 0;
    }

    /**
     * 是否有库存
     */
    public boolean hasStock() {
        return this.stock != null && this.stock > 0;
    }

    /**
     * 是否可以购买
     */
    public boolean canPurchase() {
        return isOnSale() && hasStock();
    }

    /**
     * 获取状态显示名称
     */
    public String getStatusDisplayName() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "下架";
            case 1:
                return "上架";
            default:
                return "未知";
        }
    }

    /**
     * 获取业务标识（直接返回ID）
     */
    public String getBusinessId() {
        return id;
    }

    /**
     * 是否使用UUID格式的ID
     */
    public boolean isUsingUuid() {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        // 简单的UUID格式检查：包含4个连字符的36字符字符串
        return id.length() == 36 && id.chars().filter(ch -> ch == '-').count() == 4;
    }

    /**
     * 是否使用数字格式的ID
     */
    public boolean isUsingNumericId() {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 减少库存
     * @param quantity 减少数量
     * @return 是否成功
     */
    public boolean reduceStock(int quantity) {
        if (this.stock == null || this.stock < quantity) {
            return false;
        }
        this.stock -= quantity;
        return true;
    }

    /**
     * 增加库存
     * @param quantity 增加数量
     */
    public void addStock(int quantity) {
        if (this.stock == null) {
            this.stock = 0;
        }
        this.stock += quantity;
    }

    /**
     * 检查库存是否足够
     * @param quantity 需要的数量
     * @return 是否足够
     */
    public boolean checkStock(int quantity) {
        return this.stock != null && this.stock >= quantity;
    }
}

