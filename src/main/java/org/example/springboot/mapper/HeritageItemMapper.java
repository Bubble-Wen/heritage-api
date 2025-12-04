package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.HeritageItem;
import org.example.springboot.dto.query.HeritageItemListQueryDTO;

import java.util.List;

/**
 * 非遗作品数据访问接口
 * @author system
 */
@Mapper
public interface HeritageItemMapper extends BaseMapper<HeritageItem> {

    /**
     * 分页查询非遗作品列表
     * @param page 分页对象
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<HeritageItem> selectPageWithConditions(Page<HeritageItem> page, @Param("query") HeritageItemListQueryDTO queryDTO);

    /**
     * 根据类别统计作品数量
     * @param category 类别
     * @return 数量
     */
    Long countByCategory(@Param("category") String category);

    /**
     * 根据地区统计作品数量
     * @param region 地区
     * @return 数量
     */
    Long countByRegion(@Param("region") String region);

    /**
     * 根据状态统计作品数量
     * @param status 状态
     * @return 数量
     */
    Long countByStatus(@Param("status") Integer status);

    /**
     * 根据创建人ID查询作品列表
     * @param creatorId 创建人ID
     * @return 作品列表
     */
    List<HeritageItem> selectByCreatorId(@Param("creatorId") String creatorId);

    /**
     * 获取热门作品列表
     * @param limit 限制数量
     * @return 作品列表
     */
    List<HeritageItem> selectPopularItems(@Param("limit") Integer limit);

    /**
     * 获取最新发布的作品列表
     * @param limit 限制数量
     * @return 作品列表
     */
    List<HeritageItem> selectLatestPublished(@Param("limit") Integer limit);

    /**
     * 根据关键词搜索作品
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 作品列表
     */
    List<HeritageItem> searchByKeyword(@Param("keyword") String keyword, @Param("limit") Integer limit);
}

