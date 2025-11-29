package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.ShopCategory;

import java.util.List;

/**
 * 商品分类数据访问接口
 * @author system
 */
@Mapper
public interface ShopCategoryMapper extends BaseMapper<ShopCategory> {

    /**
     * 查询所有启用的分类
     * @return 启用的分类列表
     */
    List<ShopCategory> selectEnabledCategories();

    /**
     * 统计分类下的商品数量
     * @param categoryId 分类ID
     * @return 商品数量
     */
    Long countProductsByCategory(@Param("categoryId") Long categoryId);

    /**
     * 批量更新分类状态
     * @param categoryIds 分类ID列表
     * @param status 状态
     * @return 更新数量
     */
    int batchUpdateStatus(@Param("categoryIds") List<Long> categoryIds, @Param("status") Integer status);
}
