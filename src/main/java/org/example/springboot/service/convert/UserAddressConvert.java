package org.example.springboot.service.convert;

import org.example.springboot.dto.command.UserAddressCreateCommandDTO;
import org.example.springboot.dto.command.UserAddressUpdateCommandDTO;
import org.example.springboot.dto.response.UserAddressResponseDTO;
import org.example.springboot.entity.UserAddress;

/**
 * 用户收货地址转换类
 * @author system
 */
public class UserAddressConvert {

    /**
     * CreateDTO转Entity
     */
    public static UserAddress toEntity(UserAddressCreateCommandDTO dto, Long userId) {
        if (dto == null) {
            return null;
        }
        
        UserAddress entity = new UserAddress();
        entity.setUserId(userId);
        entity.setReceiver(dto.getReceiver());
        entity.setPhone(dto.getPhone());
        entity.setProvince(dto.getProvince());
        entity.setCity(dto.getCity());
        entity.setDistrict(dto.getDistrict());
        entity.setDetail(dto.getDetail());
        entity.setIsDefault(dto.getIsDefault() != null && dto.getIsDefault() ? 1 : 0);
        
        return entity;
    }

    /**
     * UpdateDTO更新Entity
     */
    public static void updateEntity(UserAddress entity, UserAddressUpdateCommandDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        entity.setReceiver(dto.getReceiver());
        entity.setPhone(dto.getPhone());
        entity.setProvince(dto.getProvince());
        entity.setCity(dto.getCity());
        entity.setDistrict(dto.getDistrict());
        entity.setDetail(dto.getDetail());
        if (dto.getIsDefault() != null) {
            entity.setIsDefault(dto.getIsDefault() ? 1 : 0);
        }
    }

    /**
     * Entity转ResponseDTO
     */
    public static UserAddressResponseDTO toResponseDTO(UserAddress entity) {
        if (entity == null) {
            return null;
        }
        
        UserAddressResponseDTO dto = new UserAddressResponseDTO();
        dto.setId(entity.getId());
        dto.setReceiver(entity.getReceiver());
        dto.setPhone(entity.getPhone());
        dto.setProvince(entity.getProvince());
        dto.setCity(entity.getCity());
        dto.setDistrict(entity.getDistrict());
        dto.setDetail(entity.getDetail());
        dto.setFullAddress(entity.getFullAddress());
        dto.setIsDefault(entity.getIsDefault());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        
        return dto;
    }
}

