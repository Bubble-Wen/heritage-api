package org.example.springboot.service.convert;

import org.example.springboot.dto.command.ShopCategoryCreateDTO;
import org.example.springboot.dto.command.ShopCategoryUpdateDTO;
import org.example.springboot.dto.response.ShopCategoryResponseDTO;
import org.example.springboot.entity.ShopCategory;
import org.example.springboot.enums.ShopCategoryStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类转换器
 * @author system
 */
public class ShopCategoryConvert {

    /**
     * 创建DTO转实体
     */
    public static ShopCategory toEntity(ShopCategoryCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return ShopCategory.builder()
                .name(dto.getName())
                .status(dto.getStatus() != null ? dto.getStatus() : 1)
                .build();
    }

    /**
     * 更新DTO转实体
     */
    public static ShopCategory toEntity(ShopCategoryUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return ShopCategory.builder()
                .id(dto.getId())
                .name(dto.getName())
                .status(dto.getStatus())
                .build();
    }

    /**
     * 实体转响应DTO
     */
    public static ShopCategoryResponseDTO toResponseDTO(ShopCategory entity) {
        if (entity == null) {
            return null;
        }
        
        ShopCategoryResponseDTO dto = new ShopCategoryResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setStatusDesc(getStatusDesc(entity.getStatus()));
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        
        return dto;
    }

    /**
     * 实体列表转响应DTO列表
     */
    public static List<ShopCategoryResponseDTO> toResponseDTOList(List<ShopCategory> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<ShopCategoryResponseDTO> dtoList = new ArrayList<>();
        for (ShopCategory entity : entities) {
            dtoList.add(toResponseDTO(entity));
        }
        return dtoList;
    }

    /**
     * 获取状态描述
     */
    private static String getStatusDesc(Integer status) {
        ShopCategoryStatus categoryStatus = ShopCategoryStatus.getByCode(status);
        return categoryStatus != null ? categoryStatus.getDescription() : "未知";
    }
}
