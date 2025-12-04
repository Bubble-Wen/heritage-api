package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.ShopOrder;

/**
 * 订单Mapper
 * @author system
 */
@Mapper
public interface ShopOrderMapper extends BaseMapper<ShopOrder> {
}

