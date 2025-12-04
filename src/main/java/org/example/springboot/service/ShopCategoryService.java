package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.ShopCategoryCreateDTO;
import org.example.springboot.dto.command.ShopCategoryUpdateDTO;
import org.example.springboot.dto.response.ShopCategoryResponseDTO;
import org.example.springboot.entity.ShopCategory;
import org.example.springboot.enums.ShopCategoryStatus;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.ShopCategoryMapper;
import org.example.springboot.service.convert.ShopCategoryConvert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品分类服务类
 * @author system
 */
@Slf4j
@Service
public class ShopCategoryService extends ServiceImpl<ShopCategoryMapper, ShopCategory> {

    /**
     * 分页获取分类列表
     */
    public Page<ShopCategoryResponseDTO> getCategoryPage(Long current, Long size, String name, Integer status) {
        log.info("开始分页获取商品分类列表，页码: {}, 大小: {}", current, size);
        
        Page<ShopCategory> page = new Page<>(current, size);
        LambdaQueryWrapper<ShopCategory> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(ShopCategory::getName, name.trim());
        }
        if (status != null) {
            wrapper.eq(ShopCategory::getStatus, status);
        }
        
        wrapper.orderByDesc(ShopCategory::getCreateTime);
        
        Page<ShopCategory> categoryPage = page(page, wrapper);
        
        // 转换为响应DTO
        Page<ShopCategoryResponseDTO> result = new Page<>();
        result.setCurrent(categoryPage.getCurrent());
        result.setSize(categoryPage.getSize());
        result.setTotal(categoryPage.getTotal());
        result.setRecords(ShopCategoryConvert.toResponseDTOList(categoryPage.getRecords()));
        
        log.info("分页获取商品分类列表完成，共{}条记录", result.getTotal());
        return result;
    }

    /**
     * 获取所有分类列表
     */
    public List<ShopCategoryResponseDTO> getAllCategories() {
        log.info("开始获取所有商品分类列表");
        
        List<ShopCategory> categories = list(new LambdaQueryWrapper<ShopCategory>()
                .orderByDesc(ShopCategory::getCreateTime));
        
        List<ShopCategoryResponseDTO> result = ShopCategoryConvert.toResponseDTOList(categories);
        log.info("获取所有商品分类列表完成，共{}个分类", result.size());
        
        return result;
    }

    /**
     * 获取启用的分类列表
     */
    public List<ShopCategoryResponseDTO> getEnabledCategories() {
        log.info("开始获取启用的商品分类列表");
        
        List<ShopCategory> enabledCategories = list(new LambdaQueryWrapper<ShopCategory>()
                .eq(ShopCategory::getStatus, ShopCategoryStatus.ENABLED.getCode())
                .orderByDesc(ShopCategory::getCreateTime));
        
        List<ShopCategoryResponseDTO> result = ShopCategoryConvert.toResponseDTOList(enabledCategories);
        log.info("获取启用的商品分类列表完成，共{}个分类", result.size());
        
        return result;
    }

    /**
     * 根据ID获取分类详情
     */
    public ShopCategoryResponseDTO getCategoryById(Long id) {
        log.info("开始获取分类详情，ID: {}", id);
        
        ShopCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        ShopCategoryResponseDTO result = ShopCategoryConvert.toResponseDTO(category);
        log.info("获取分类详情完成，分类名称: {}", result.getName());
        
        return result;
    }

    /**
     * 创建分类
     */
    @Transactional(rollbackFor = Exception.class)
    public void createCategory(ShopCategoryCreateDTO dto) {
        log.info("开始创建商品分类，名称: {}", dto.getName());
        
        // 验证分类名称唯一性
        validateCategoryNameUnique(dto.getName(), null);
        
        // 转换并保存
        ShopCategory category = ShopCategoryConvert.toEntity(dto);
        save(category);
        
        log.info("创建商品分类成功，ID: {}", category.getId());
    }

    /**
     * 更新分类
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(ShopCategoryUpdateDTO dto) {
        log.info("开始更新商品分类，ID: {}", dto.getId());
        
        // 检查分类是否存在
        ShopCategory existingCategory = getById(dto.getId());
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 验证分类名称唯一性
        validateCategoryNameUnique(dto.getName(), dto.getId());
        
        // 更新分类
        ShopCategory category = ShopCategoryConvert.toEntity(dto);
        updateById(category);
        
        log.info("更新商品分类成功");
    }

    /**
     * 删除分类
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        log.info("开始删除商品分类，ID: {}", id);
        
        // 检查分类是否存在
        ShopCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查是否有关联商品
        Long productCount = baseMapper.countProductsByCategory(id);
        if (productCount > 0) {
            throw new BusinessException("该分类下存在商品，无法删除");
        }
        
        // 删除分类
        removeById(id);
        
        log.info("删除商品分类成功");
    }

    /**
     * 更新分类状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryStatus(Long id, Integer status) {
        log.info("开始更新分类状态，ID: {}, 状态: {}", id, status);
        
        // 验证状态值
        ShopCategoryStatus categoryStatus = ShopCategoryStatus.getByCode(status);
        if (categoryStatus == null) {
            throw new BusinessException("无效的状态值");
        }
        
        // 检查分类是否存在
        ShopCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 更新分类状态
        category.setStatus(status);
        updateById(category);
        
        log.info("更新分类状态成功");
    }

    /**
     * 批量更新分类状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        log.info("开始批量更新分类状态，数量: {}, 状态: {}", ids.size(), status);
        
        // 验证状态值
        ShopCategoryStatus categoryStatus = ShopCategoryStatus.getByCode(status);
        if (categoryStatus == null) {
            throw new BusinessException("无效的状态值");
        }
        
        // 批量更新
        int updateCount = baseMapper.batchUpdateStatus(ids, status);
        
        log.info("批量更新分类状态成功，更新数量: {}", updateCount);
    }

    /**
     * 验证分类名称唯一性
     */
    private void validateCategoryNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<ShopCategory> queryWrapper = new LambdaQueryWrapper<ShopCategory>()
                .eq(ShopCategory::getName, name);
        
        if (excludeId != null) {
            queryWrapper.ne(ShopCategory::getId, excludeId);
        }
        
        long count = count(queryWrapper);
        if (count > 0) {
            throw new BusinessException("分类名称已存在");
        }
    }
}