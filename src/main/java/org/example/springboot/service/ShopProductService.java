package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.ShopProductCreateCommandDTO;
import org.example.springboot.dto.command.ShopProductUpdateCommandDTO;
import org.example.springboot.dto.query.ShopProductListQueryDTO;
import org.example.springboot.dto.response.ShopProductDetailResponseDTO;
import org.example.springboot.dto.response.ShopProductImageResponseDTO;
import org.example.springboot.dto.response.ShopProductListResponseDTO;
import org.example.springboot.entity.ShopCategory;
import org.example.springboot.entity.ShopProduct;
import org.example.springboot.entity.SysFileInfo;
import org.example.springboot.enums.FileBusinessTypeEnum;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.ShopProductMapper;
import org.example.springboot.mapper.SysFileInfoMapper;
import org.example.springboot.service.convert.ShopProductConvert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品服务类
 * @author system
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopProductService extends ServiceImpl<ShopProductMapper, ShopProduct> {

    private final ShopCategoryService shopCategoryService;
    private final SysFileInfoMapper sysFileInfoMapper;

    /**
     * 创建商品
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopProductDetailResponseDTO createProduct(ShopProductCreateCommandDTO createDTO) {
        log.info("开始创建商品，标题: {}", createDTO.getTitle());

        // 验证分类是否存在
        ShopCategory category = shopCategoryService.getById(createDTO.getCategoryId());
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }

        // 转换并保存
        ShopProduct product = ShopProductConvert.createCommandToEntity(createDTO);
        save(product);

        log.info("商品创建成功，ID: {}", product.getId());
        return getProductDetailById(product.getId());
    }

    /**
     * 更新商品
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopProductDetailResponseDTO updateProduct(String id, ShopProductUpdateCommandDTO updateDTO) {
        log.info("开始更新商品，ID: {}", id);

        // 检查商品是否存在
        ShopProduct product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 验证分类是否存在
        if (updateDTO.getCategoryId() != null) {
            ShopCategory category = shopCategoryService.getById(updateDTO.getCategoryId());
            if (category == null) {
                throw new BusinessException("商品分类不存在");
            }
        }

        // 应用更新
        ShopProductConvert.applyUpdateToEntity(product, updateDTO);
        updateById(product);

        log.info("商品更新成功，ID: {}", id);
        return getProductDetailById(id);
    }

    /**
     * 删除商品
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(String id) {
        log.info("开始删除商品，ID: {}", id);

        ShopProduct product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        removeById(id);
        log.info("商品删除成功，ID: {}", id);
    }

    /**
     * 获取商品详情
     */
    public ShopProductDetailResponseDTO getProductDetailById(String id) {
        log.info("开始获取商品详情，ID: {}", id);

        ShopProduct product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        ShopProductDetailResponseDTO response = ShopProductConvert.entityToDetailResponse(product);

        // 填充分类名称
        if (product.getCategoryId() != null) {
            ShopCategory category = shopCategoryService.getById(product.getCategoryId());
            if (category != null) {
                ShopProductConvert.fillCategoryName(response, category.getName());
            }
        }

        // 填充封面文件路径（从文件表查询）
        SysFileInfo coverFile = sysFileInfoMapper.selectOne(
            new LambdaQueryWrapper<SysFileInfo>()
                .eq(SysFileInfo::getBusinessType, FileBusinessTypeEnum.SHOP_PRODUCT.name())
                .eq(SysFileInfo::getBusinessId, product.getId())
                .eq(SysFileInfo::getBusinessField, "cover")
                .eq(SysFileInfo::getStatus, 1)
                .orderByDesc(SysFileInfo::getCreateTime)
                .last("LIMIT 1")
        );
        if (coverFile != null) {
            response.setCoverFileId(coverFile.getId());
            ShopProductConvert.fillCoverFilePath(response, coverFile.getFilePath());
        }

        // 填充商品图片列表（从文件表查询）
        fillProductImages(response, product.getId());

        log.info("获取商品详情成功，ID: {}", id);
        return response;
    }

    /**
     * 分页查询商品列表
     */
    public Page<ShopProductListResponseDTO> getProductPage(ShopProductListQueryDTO queryDTO) {
        log.info("开始分页查询商品列表，页码: {}, 大小: {}", queryDTO.getPage(), queryDTO.getPageSize());

        Page<ShopProduct> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        LambdaQueryWrapper<ShopProduct> wrapper = new LambdaQueryWrapper<>();

        // 标题模糊搜索
        if (queryDTO.getTitle() != null && !queryDTO.getTitle().trim().isEmpty()) {
            wrapper.like(ShopProduct::getTitle, queryDTO.getTitle().trim());
        }

        // 分类筛选
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(ShopProduct::getCategoryId, queryDTO.getCategoryId());
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(ShopProduct::getStatus, queryDTO.getStatus());
        }

        // 价格范围筛选
        if (queryDTO.getMinPrice() != null) {
            wrapper.ge(ShopProduct::getPrice, queryDTO.getMinPrice());
        }
        if (queryDTO.getMaxPrice() != null) {
            wrapper.le(ShopProduct::getPrice, queryDTO.getMaxPrice());
        }

        // 库存筛选
        if (queryDTO.getHasStock() != null) {
            if (queryDTO.getHasStock()) {
                wrapper.gt(ShopProduct::getStock, 0);
            } else {
                wrapper.eq(ShopProduct::getStock, 0);
            }
        }

        // 排序
        String sortField = queryDTO.getSortField();
        String sortOrder = queryDTO.getSortOrder();
        if ("price".equals(sortField)) {
            wrapper.orderBy(true, "asc".equalsIgnoreCase(sortOrder), ShopProduct::getPrice);
        } else if ("stock".equals(sortField)) {
            wrapper.orderBy(true, "asc".equalsIgnoreCase(sortOrder), ShopProduct::getStock);
        } else {
            // 默认按创建时间降序（最新在前）
            wrapper.orderByDesc(ShopProduct::getCreateTime);
        }

        Page<ShopProduct> productPage = page(page, wrapper);

        // 转换为响应DTO
        Page<ShopProductListResponseDTO> result = new Page<>();
        result.setCurrent(productPage.getCurrent());
        result.setSize(productPage.getSize());
        result.setTotal(productPage.getTotal());

        List<ShopProductListResponseDTO> responseList = productPage.getRecords().stream()
                .map(ShopProductConvert::entityToListResponse)
                .collect(Collectors.toList());

        // 批量填充分类名称
        fillCategoryNames(responseList);

        // 批量填充封面文件路径
        fillCoverFilePaths(responseList);

        result.setRecords(responseList);

        log.info("分页查询商品列表完成，共{}条记录", result.getTotal());
        return result;
    }

    /**
     * 上架商品
     */
    @Transactional(rollbackFor = Exception.class)
    public void onShelfProduct(String id) {
        log.info("开始上架商品，ID: {}", id);

        ShopProduct product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(1);
        updateById(product);

        log.info("商品上架成功，ID: {}", id);
    }

    /**
     * 下架商品
     */
    @Transactional(rollbackFor = Exception.class)
    public void offShelfProduct(String id) {
        log.info("开始下架商品，ID: {}", id);

        ShopProduct product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(0);
        updateById(product);

        log.info("商品下架成功，ID: {}", id);
    }

    /**
     * 更新商品库存
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(String id, Integer quantity) {
        log.info("开始更新商品库存，ID: {}, 数量: {}", id, quantity);

        ShopProduct product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStock(quantity);
        updateById(product);

        log.info("商品库存更新成功，ID: {}", id);
    }

    /**
     * 根据ID列表批量获取商品信息
     * @param productIds 商品ID列表
     * @return 商品列表响应DTO
     */
    public List<ShopProductListResponseDTO> getProductsByIds(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        log.info("开始批量获取商品信息，数量: {}", productIds.size());

        LambdaQueryWrapper<ShopProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ShopProduct::getId, productIds);
        
        List<ShopProduct> products = list(wrapper);
        
        // 转换为响应DTO
        List<ShopProductListResponseDTO> responseList = products.stream()
                .map(ShopProductConvert::entityToListResponse)
                .collect(Collectors.toList());

        // 批量填充分类名称
        fillCategoryNames(responseList);

        // 批量填充封面文件路径
        fillCoverFilePaths(responseList);

        // 按照原始ID列表的顺序排序
        Map<String, ShopProductListResponseDTO> productMap = responseList.stream()
                .collect(Collectors.toMap(ShopProductListResponseDTO::getId, dto -> dto));
        
        List<ShopProductListResponseDTO> sortedList = productIds.stream()
                .map(productMap::get)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        log.info("批量获取商品信息完成，返回数量: {}", sortedList.size());
        return sortedList;
    }

    /**
     * 批量填充分类名称
     */
    private void fillCategoryNames(List<ShopProductListResponseDTO> responseList) {
        if (responseList == null || responseList.isEmpty()) {
            return;
        }

        // 收集所有分类ID
        List<Long> categoryIds = responseList.stream()
                .map(ShopProductListResponseDTO::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        if (categoryIds.isEmpty()) {
            return;
        }

        // 批量查询分类
        List<ShopCategory> categories = shopCategoryService.listByIds(categoryIds);
        Map<Long, String> categoryNameMap = categories.stream()
                .collect(Collectors.toMap(ShopCategory::getId, ShopCategory::getName));

        // 填充分类名称
        responseList.forEach(response -> {
            String categoryName = categoryNameMap.get(response.getCategoryId());
            if (categoryName != null) {
                ShopProductConvert.fillCategoryName(response, categoryName);
            }
        });
    }

    /**
     * 批量填充封面文件路径
     */
    private void fillCoverFilePaths(List<ShopProductListResponseDTO> responseList) {
        if (responseList == null || responseList.isEmpty()) {
            return;
        }

        // 收集所有商品ID
        List<String> productIds = responseList.stream()
                .map(ShopProductListResponseDTO::getId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return;
        }

        // 批量查询封面文件
        List<SysFileInfo> files = sysFileInfoMapper.selectList(
            new LambdaQueryWrapper<SysFileInfo>()
                .eq(SysFileInfo::getBusinessType, FileBusinessTypeEnum.SHOP_PRODUCT.name())
                .in(SysFileInfo::getBusinessId, productIds)
                .eq(SysFileInfo::getBusinessField, "cover")
                .eq(SysFileInfo::getStatus, 1)
        );

        // 按商品ID分组，每个商品只取最新的一个封面
        Map<String, SysFileInfo> fileMap = files.stream()
                .collect(Collectors.toMap(
                    SysFileInfo::getBusinessId,
                    file -> file,
                    (existing, replacement) -> 
                        existing.getCreateTime().isAfter(replacement.getCreateTime()) ? existing : replacement
                ));

        // 填充文件路径和文件ID
        responseList.forEach(response -> {
            SysFileInfo file = fileMap.get(response.getId());
            if (file != null) {
                response.setCoverFileId(file.getId());
                ShopProductConvert.fillCoverFilePath(response, file.getFilePath());
            }
        });
    }

    /**
     * 填充商品图片列表
     */
    private void fillProductImages(ShopProductDetailResponseDTO response, String productId) {
        if (response == null || productId == null) {
            return;
        }

        // 查询商品的所有图片（business_field='images'）
        List<SysFileInfo> imageFiles = sysFileInfoMapper.selectList(
            new LambdaQueryWrapper<SysFileInfo>()
                .eq(SysFileInfo::getBusinessType, FileBusinessTypeEnum.SHOP_PRODUCT.name())
                .eq(SysFileInfo::getBusinessId, productId)
                .eq(SysFileInfo::getBusinessField, "images")
                .eq(SysFileInfo::getStatus, 1)
            
                .orderByDesc(SysFileInfo::getCreateTime)
        );

        // 转换为 DTO
        List<ShopProductImageResponseDTO> imageList = new ArrayList<>();
        for (SysFileInfo file : imageFiles) {
            ShopProductImageResponseDTO imageDTO = ShopProductImageResponseDTO.builder()
                .id(file.getId())
                .filePath(file.getFilePath())
                .originalName(file.getOriginalName())

                .build();
            imageList.add(imageDTO);
        }

        response.setImageList(imageList);
        log.debug("填充商品图片列表完成，商品ID: {}, 图片数量: {}", productId, imageList.size());
    }
}

