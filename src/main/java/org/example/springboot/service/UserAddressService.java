package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.UserAddressCreateCommandDTO;
import org.example.springboot.dto.command.UserAddressUpdateCommandDTO;
import org.example.springboot.dto.response.UserAddressResponseDTO;
import org.example.springboot.entity.UserAddress;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.UserAddressMapper;
import org.example.springboot.service.convert.UserAddressConvert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户收货地址服务
 * @author system
 */
@Slf4j
@Service
public class UserAddressService {

    @Resource
    private UserAddressMapper userAddressMapper;

    /**
     * 创建收货地址
     */
    @Transactional(rollbackFor = Exception.class)
    public UserAddressResponseDTO createAddress(UserAddressCreateCommandDTO dto, Long userId) {
        log.info("创建收货地址，用户ID: {}, 收货人: {}", userId, dto.getReceiver());
        
        // 如果设置为默认地址，先取消该用户的其他默认地址
        if (dto.getIsDefault() != null && dto.getIsDefault()) {
            unsetUserDefaultAddress(userId);
        }
        
        // 创建地址
        UserAddress address = UserAddressConvert.toEntity(dto, userId);
        userAddressMapper.insert(address);
        
        log.info("收货地址创建成功，地址ID: {}", address.getId());
        return UserAddressConvert.toResponseDTO(address);
    }

    /**
     * 更新收货地址
     */
    @Transactional(rollbackFor = Exception.class)
    public UserAddressResponseDTO updateAddress(Long addressId, UserAddressUpdateCommandDTO dto, Long userId) {
        log.info("更新收货地址，地址ID: {}, 用户ID: {}", addressId, userId);
        
        // 查询地址
        UserAddress address = getAddressById(addressId, userId);
        
        // 如果设置为默认地址，先取消该用户的其他默认地址
        if (dto.getIsDefault() != null && dto.getIsDefault()) {
            unsetUserDefaultAddress(userId);
        }
        
        // 更新地址
        UserAddressConvert.updateEntity(address, dto);
        userAddressMapper.updateById(address);
        
        log.info("收货地址更新成功，地址ID: {}", addressId);
        return UserAddressConvert.toResponseDTO(address);
    }

    /**
     * 删除收货地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long addressId, Long userId) {
        log.info("删除收货地址，地址ID: {}, 用户ID: {}", addressId, userId);
        
        // 查询地址（验证权限）
        UserAddress address = getAddressById(addressId, userId);
        
        // 删除地址
        userAddressMapper.deleteById(addressId);
        
        log.info("收货地址删除成功，地址ID: {}", addressId);
    }

    /**
     * 设置默认地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long addressId, Long userId) {
        log.info("设置默认地址，地址ID: {}, 用户ID: {}", addressId, userId);
        
        // 查询地址（验证权限）
        UserAddress address = getAddressById(addressId, userId);
        
        // 先取消该用户的其他默认地址
        unsetUserDefaultAddress(userId);
        
        // 设置为默认地址
        address.setIsDefault(1);
        userAddressMapper.updateById(address);
        
        log.info("默认地址设置成功，地址ID: {}", addressId);
    }

    /**
     * 获取用户所有收货地址
     */
    public List<UserAddressResponseDTO> getUserAddressList(Long userId) {
        log.info("获取用户收货地址列表，用户ID: {}", userId);
        
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId);
        wrapper.orderByDesc(UserAddress::getIsDefault);
        wrapper.orderByDesc(UserAddress::getCreateTime);
        
        List<UserAddress> addresses = userAddressMapper.selectList(wrapper);
        
        return addresses.stream()
                .map(UserAddressConvert::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户默认收货地址
     */
    public UserAddressResponseDTO getUserDefaultAddress(Long userId) {
        log.info("获取用户默认收货地址，用户ID: {}", userId);
        
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId);
        wrapper.eq(UserAddress::getIsDefault, 1);
        wrapper.last("LIMIT 1");
        
        UserAddress address = userAddressMapper.selectOne(wrapper);
        
        return UserAddressConvert.toResponseDTO(address);
    }

    /**
     * 获取地址详情
     */
    public UserAddressResponseDTO getAddressDetail(Long addressId, Long userId) {
        log.info("获取收货地址详情，地址ID: {}, 用户ID: {}", addressId, userId);
        
        UserAddress address = getAddressById(addressId, userId);
        return UserAddressConvert.toResponseDTO(address);
    }

    /**
     * 根据ID获取地址（内部方法，验证权限）
     */
    private UserAddress getAddressById(Long addressId, Long userId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该收货地址");
        }
        return address;
    }

    /**
     * 取消用户的默认地址（内部方法）
     */
    private void unsetUserDefaultAddress(Long userId) {
        LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId);
        wrapper.eq(UserAddress::getIsDefault, 1);
        wrapper.set(UserAddress::getIsDefault, 0);
        
        userAddressMapper.update(null, wrapper);
    }

    /**
     * 根据ID获取地址Entity（供其他Service调用）
     */
    public UserAddress getAddressEntityById(Long addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }
        return address;
    }
}

