package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单响应DTO")
public class OrderResponseDTO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "订单状态码")
    private Integer status;

    @Schema(description = "订单状态名称")
    private String statusName;

    @Schema(description = "支付方式")
    private String payType;

    @Schema(description = "支付方式名称")
    private String payTypeName;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "收货地址")
    private UserAddressResponseDTO address;

    @Schema(description = "物流单号")
    private String logisticsNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "订单商品明细")
    private List<OrderItemResponseDTO> items;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

