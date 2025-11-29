package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
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
 * 订单实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("shop_order")
@Schema(description = "订单实体类")
public class ShopOrder {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    @TableField("order_no")
    private String orderNo;

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "订单总金额")
    @NotNull(message = "订单总金额不能为空")
    @DecimalMin(value = "0.00", message = "订单总金额不能小于0")
    @TableField("total_amount")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额")
    @NotNull(message = "实付金额不能为空")
    @DecimalMin(value = "0.00", message = "实付金额不能小于0")
    @TableField("pay_amount")
    private BigDecimal payAmount;

    @Schema(description = "订单状态 0待支付 1已支付 2已发货 3已完成 4已关闭")
    private Integer status;

    @Schema(description = "支付方式 ALI/WECHAT/OTHER")
    @TableField("pay_type")
    private String payType;

    @Schema(description = "支付时间")
    @TableField("pay_time")
    private LocalDateTime payTime;

    @Schema(description = "收货地址ID")
    @TableField("receiver_address_id")
    private Long receiverAddressId;

    @Schema(description = "物流单号")
    @TableField("logistics_no")
    private String logisticsNo;

    @Schema(description = "备注")
    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否待支付
     */
    public boolean isPending() {
        return this.status != null && this.status == 0;
    }

    /**
     * 是否已支付
     */
    public boolean isPaid() {
        return this.status != null && this.status == 1;
    }

    /**
     * 是否已发货
     */
    public boolean isShipped() {
        return this.status != null && this.status == 2;
    }

    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return this.status != null && this.status == 3;
    }

    /**
     * 是否已关闭
     */
    public boolean isClosed() {
        return this.status != null && this.status == 4;
    }

    /**
     * 能否取消
     */
    public boolean canCancel() {
        return isPending() || isPaid();
    }

    /**
     * 能否发货
     */
    public boolean canShip() {
        return isPaid();
    }

    /**
     * 能否确认收货
     */
    public boolean canConfirm() {
        return isShipped();
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
                return "待支付";
            case 1:
                return "已支付";
            case 2:
                return "已发货";
            case 3:
                return "已完成";
            case 4:
                return "已关闭";
            default:
                return "未知";
        }
    }
}

