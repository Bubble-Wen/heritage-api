package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.ShopProduct;

/**
 * 商品数据访问接口
 * @author system
 */
@Mapper
public interface ShopProductMapper extends BaseMapper<ShopProduct> {
}

