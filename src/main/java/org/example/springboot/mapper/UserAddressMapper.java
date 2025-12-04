package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.UserAddress;

/**
 * 用户收货地址Mapper
 * @author system
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}

