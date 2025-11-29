package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.ShopOrderItem;

/**
 * 订单明细Mapper
 * @author system
 */
@Mapper
public interface ShopOrderItemMapper extends BaseMapper<ShopOrderItem> {
}

