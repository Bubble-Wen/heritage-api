package org.example.springboot.service.convert;

import org.example.springboot.dto.response.OrderItemResponseDTO;
import org.example.springboot.dto.response.OrderListResponseDTO;
import org.example.springboot.dto.response.OrderResponseDTO;
import org.example.springboot.dto.response.UserAddressResponseDTO;
import org.example.springboot.entity.ShopOrder;
import org.example.springboot.entity.ShopOrderItem;
import org.example.springboot.entity.UserAddress;
import org.example.springboot.enums.OrderStatus;
import org.example.springboot.enums.PayType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单转换类
 * @author system
 */
public class OrderConvert {

    /**
     * Entity转ResponseDTO
     */
    public static OrderResponseDTO toResponseDTO(ShopOrder order, UserAddress address, List<ShopOrderItem> items) {
        if (order == null) {
            return null;
        }
        
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPayAmount(order.getPayAmount());
        dto.setStatus(order.getStatus());
        dto.setStatusName(OrderStatus.getDescription(order.getStatus()));
        dto.setPayType(order.getPayType());
        dto.setPayTypeName(PayType.getDescription(order.getPayType()));
        dto.setPayTime(order.getPayTime());
        dto.setLogisticsNo(order.getLogisticsNo());
        dto.setRemark(order.getRemark());
        dto.setCreateTime(order.getCreateTime());
        dto.setUpdateTime(order.getUpdateTime());
        
        // 转换地址
        if (address != null) {
            dto.setAddress(UserAddressConvert.toResponseDTO(address));
        }
        
        // 转换订单明细
        if (items != null && !items.isEmpty()) {
            List<OrderItemResponseDTO> itemDTOs = items.stream()
                    .map(OrderConvert::toItemResponseDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }
        
        return dto;
    }

    /**
     * OrderItem Entity转ResponseDTO
     */
    public static OrderItemResponseDTO toItemResponseDTO(ShopOrderItem item) {
        if (item == null) {
            return null;
        }
        
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setSkuId(item.getSkuId());
        dto.setTitle(item.getTitle());
        dto.setSkuTitle(item.getSkuTitle());
        dto.setFullTitle(item.getFullTitle());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());
        
        return dto;
    }

    /**
     * Entity转ListResponseDTO
     */
    public static OrderListResponseDTO toListResponseDTO(ShopOrder order, List<ShopOrderItem> items) {
        if (order == null) {
            return null;
        }
        
        OrderListResponseDTO dto = new OrderListResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPayAmount(order.getPayAmount());
        dto.setStatus(order.getStatus());
        dto.setStatusName(OrderStatus.getDescription(order.getStatus()));
        dto.setPayTime(order.getPayTime());
        dto.setCreateTime(order.getCreateTime());
        
        // 订单商品数量和主商品标题
        if (items != null && !items.isEmpty()) {
            dto.setItemCount(items.size());
            dto.setMainProductTitle(items.get(0).getTitle());
        } else {
            dto.setItemCount(0);
            dto.setMainProductTitle("");
        }
        
        return dto;
    }
}

