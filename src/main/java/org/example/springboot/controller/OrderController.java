package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.OrderCreateCommandDTO;
import org.example.springboot.dto.response.OrderListResponseDTO;
import org.example.springboot.dto.response.OrderResponseDTO;
import org.example.springboot.enums.UserType;
import org.example.springboot.service.OrderService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * @author system
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/shop/order")
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<OrderResponseDTO> createOrder(@Valid @RequestBody OrderCreateCommandDTO dto) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("创建订单，用户ID: {}, 商品ID: {}", userId, dto.getProductId());
        OrderResponseDTO result = orderService.createOrder(dto, userId);
        return Result.success(result);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderResponseDTO> getOrderDetail(@Parameter(description = "订单ID") @PathVariable Long id) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        String userType = JwtTokenUtils.getCurrentUserType();
        boolean isAdmin = UserType.ADMIN.getCode().equals(userType);
        
        log.info("获取订单详情，订单ID: {}, 用户ID: {}, 角色: {}", id, userId, userType);
        OrderResponseDTO result = orderService.getOrderDetail(id, userId, isAdmin);
        return Result.success(result);
    }

    @Operation(summary = "分页查询用户订单列表")
    @GetMapping("/page")
    public Result<Page<OrderListResponseDTO>> getUserOrderPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("分页查询用户订单，用户ID: {}, 页码: {}, 大小: {}, 状态: {}", userId, current, size, status);
        Page<OrderListResponseDTO> result = orderService.getUserOrderPage(current, size, status, userId);
        return Result.success(result);
    }

    @Operation(summary = "分页查询管理员订单列表")
    @GetMapping("/admin/page")
    public Result<Page<OrderListResponseDTO>> getAdminOrderPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo) {
        log.info("分页查询管理员订单，页码: {}, 大小: {}, 状态: {}, 订单号: {}", current, size, status, orderNo);
        Page<OrderListResponseDTO> result = orderService.getAdminOrderPage(current, size, status, orderNo);
        return Result.success(result);
    }

    @Operation(summary = "支付订单")
    @PutMapping("/{id}/pay")
    public Result<Void> payOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "支付方式") @RequestParam String payType) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("支付订单，订单ID: {}, 用户ID: {}, 支付方式: {}", id, userId, payType);
        orderService.payOrder(id, userId, payType);
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@Parameter(description = "订单ID") @PathVariable Long id) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        String userType = JwtTokenUtils.getCurrentUserType();
        boolean isAdmin = UserType.ADMIN.getCode().equals(userType);
        
        log.info("取消订单，订单ID: {}, 用户ID: {}, 是否管理员: {}", id, userId, isAdmin);
        orderService.cancelOrder(id, userId, isAdmin);
        return Result.success();
    }

    @Operation(summary = "发货（管理员）")
    @PutMapping("/{id}/ship")
    public Result<Void> shipOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "物流单号") @RequestParam String logisticsNo) {
        log.info("订单发货，订单ID: {}, 物流单号: {}", id, logisticsNo);
        orderService.shipOrder(id, logisticsNo);
        return Result.success();
    }

    @Operation(summary = "确认收货")
    @PutMapping("/{id}/confirm")
    public Result<Void> confirmOrder(@Parameter(description = "订单ID") @PathVariable Long id) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("确认收货，订单ID: {}, 用户ID: {}", id, userId);
        orderService.confirmOrder(id, userId);
        return Result.success();
    }
}

