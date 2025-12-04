package org.example.springboot.service.convert;

import org.example.springboot.dto.command.ShopProductCreateCommandDTO;
import org.example.springboot.dto.command.ShopProductUpdateCommandDTO;
import org.example.springboot.dto.response.ShopProductDetailResponseDTO;
import org.example.springboot.dto.response.ShopProductListResponseDTO;
import org.example.springboot.entity.ShopProduct;

/**
 * 商品DTO转换工具类
 * @author system
 */
public class ShopProductConvert {

    /**
     * 创建命令DTO转实体
     */
    public static ShopProduct createCommandToEntity(ShopProductCreateCommandDTO createDTO) {
        return ShopProduct.builder()
                .id(createDTO.getId()) // 使用前端传来的UUID
                .title(createDTO.getTitle())
                .subtitle(createDTO.getSubtitle())
                .categoryId(createDTO.getCategoryId())
                .price(createDTO.getPrice())
                .stock(createDTO.getStock())
                .detail(createDTO.getDetail())
                .status(createDTO.getStatus() != null ? createDTO.getStatus() : 1) // 默认上架
                .build();
    }

    /**
     * 实体转详情响应DTO
     */
    public static ShopProductDetailResponseDTO entityToDetailResponse(ShopProduct product) {
        if (product == null) {
            return null;
        }

        ShopProductDetailResponseDTO response = new ShopProductDetailResponseDTO();
        response.setId(product.getId());
        response.setTitle(product.getTitle());
        response.setSubtitle(product.getSubtitle());
        response.setCategoryId(product.getCategoryId());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setDetail(product.getDetail());
        response.setStatus(product.getStatus());
        response.setStatusName(product.getStatusDisplayName());
        response.setCreateTime(product.getCreateTime());
        response.setUpdateTime(product.getUpdateTime());

        return response;
    }

    /**
     * 实体转列表响应DTO
     */
    public static ShopProductListResponseDTO entityToListResponse(ShopProduct product) {
        if (product == null) {
            return null;
        }

        ShopProductListResponseDTO response = new ShopProductListResponseDTO();
        response.setId(product.getId());
        response.setTitle(product.getTitle());
        response.setSubtitle(product.getSubtitle());
        response.setCategoryId(product.getCategoryId());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setStatus(product.getStatus());
        response.setStatusName(product.getStatusDisplayName());
        response.setCreateTime(product.getCreateTime());
        response.setUpdateTime(product.getUpdateTime());

        return response;
    }

    /**
     * 应用更新到实体
     */
    public static void applyUpdateToEntity(ShopProduct product, ShopProductUpdateCommandDTO updateDTO) {
        if (updateDTO.getTitle() != null) {
            product.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getSubtitle() != null) {
            product.setSubtitle(updateDTO.getSubtitle());
        }
        if (updateDTO.getCategoryId() != null) {
            product.setCategoryId(updateDTO.getCategoryId());
        }
        if (updateDTO.getPrice() != null) {
            product.setPrice(updateDTO.getPrice());
        }
        if (updateDTO.getStock() != null) {
            product.setStock(updateDTO.getStock());
        }
        if (updateDTO.getDetail() != null) {
            product.setDetail(updateDTO.getDetail());
        }
        if (updateDTO.getStatus() != null) {
            product.setStatus(updateDTO.getStatus());
        }
    }

    /**
     * 填充分类名称
     */
    public static void fillCategoryName(ShopProductDetailResponseDTO response, String categoryName) {
        if (response != null) {
            response.setCategoryName(categoryName);
        }
    }

    /**
     * 填充分类名称（列表）
     */
    public static void fillCategoryName(ShopProductListResponseDTO response, String categoryName) {
        if (response != null) {
            response.setCategoryName(categoryName);
        }
    }

    /**
     * 填充封面文件路径
     */
    public static void fillCoverFilePath(ShopProductDetailResponseDTO response, String filePath) {
        if (response != null) {
            response.setCoverFilePath(filePath);
        }
    }

    /**
     * 填充封面文件路径（列表）
     */
    public static void fillCoverFilePath(ShopProductListResponseDTO response, String filePath) {
        if (response != null) {
            response.setCoverFilePath(filePath);
        }
    }
}

