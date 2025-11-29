package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.example.springboot.common.Result;
import org.example.springboot.dto.command.HeritageItemCreateCommandDTO;
import org.example.springboot.dto.command.HeritageItemUpdateCommandDTO;
import org.example.springboot.dto.query.HeritageItemListQueryDTO;
import org.example.springboot.dto.response.HeritageItemDetailResponseDTO;
// HeritageItemMediaResponseDTO 仍然保留，用于返回媒体文件信息
import org.example.springboot.service.HeritageItemService;
// HeritageItemMediaService 已移除

/**
 * 非遗作品管理控制器
 * @author system
 */
@Tag(name = "非遗作品管理", description = "非遗作品的增删改查、发布、下架等操作")
@RequestMapping("/heritage-item")
@RestController
@Slf4j
@Validated
public class HeritageItemController {

    @Resource
    private HeritageItemService heritageItemService;

    // HeritageItemMediaService 已移除，媒体文件直接通过 sys_file_info 表管理

    /**
     * 创建非遗作品
     */
    @Operation(summary = "创建非遗作品", description = "创建新的非遗作品")
    @PostMapping("/create")
    public Result<HeritageItemDetailResponseDTO> createHeritageItem(
            @Valid @RequestBody HeritageItemCreateCommandDTO createDTO) {
        log.info("创建非遗作品请求: title={}, category={}", createDTO.getTitle(), createDTO.getCategory());
        HeritageItemDetailResponseDTO response = heritageItemService.createHeritageItem(createDTO);
        return Result.success("作品创建成功", response);
    }

    /**
     * 根据ID获取非遗作品详情
     */
    @Operation(summary = "获取作品详情", description = "根据作品ID获取详细信息")
    @GetMapping("/{itemId}")
    public Result<HeritageItemDetailResponseDTO> getHeritageItemById(
            @Parameter(description = "作品ID") @PathVariable String itemId) {
        log.info("获取作品详情请求: itemId={}", itemId);
        HeritageItemDetailResponseDTO response = heritageItemService.getHeritageItemById(itemId);
        return Result.success(response);
    }

    /**
     * 分页查询非遗作品列表
     */
    @Operation(summary = "分页查询作品列表", description = "根据条件分页查询非遗作品列表")
    @GetMapping("/page")
    public Result<Page<HeritageItemDetailResponseDTO>> getHeritageItemPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "标题关键词") @RequestParam(required = false) String title,
            @Parameter(description = "类别") @RequestParam(required = false) String category,
            @Parameter(description = "地区") @RequestParam(required = false) String region,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "创建人ID") @RequestParam(required = false) String creatorId,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endDate,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "create_time") String orderBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String orderDirection) {
        
        log.info("分页查询作品列表: page={}, size={}, title={}, category={}", 
                currentPage, size, title, category);

        HeritageItemListQueryDTO queryDTO = new HeritageItemListQueryDTO();
        queryDTO.setCurrentPage(currentPage);
        queryDTO.setSize(size);
        queryDTO.setTitle(title);
        queryDTO.setCategory(category);
        queryDTO.setRegion(region);
        queryDTO.setStatus(status);
        queryDTO.setCreatorId(creatorId);
        queryDTO.setStartDate(startDate);
        queryDTO.setEndDate(endDate);
        queryDTO.setOrderBy(orderBy);
        queryDTO.setOrderDirection(orderDirection);

        Page<HeritageItemDetailResponseDTO> response = heritageItemService.getHeritageItemPage(queryDTO);
        return Result.success(response);
    }

    /**
     * 更新非遗作品
     */
    @Operation(summary = "更新作品信息", description = "更新非遗作品的基本信息")
    @PutMapping("/{itemId}")
    public Result<HeritageItemDetailResponseDTO> updateHeritageItem(
            @Parameter(description = "作品ID") @PathVariable String itemId,
            @Valid @RequestBody HeritageItemUpdateCommandDTO updateDTO) {
        log.info("更新作品信息请求: itemId={}, title={}", itemId, updateDTO.getTitle());
        HeritageItemDetailResponseDTO response = heritageItemService.updateHeritageItem(itemId, updateDTO);
        return Result.success("作品更新成功", response);
    }

    /**
     * 删除非遗作品
     */
    @Operation(summary = "删除作品", description = "删除指定的非遗作品")
    @DeleteMapping("/{itemId}")
    public Result<Void> deleteHeritageItem(
            @Parameter(description = "作品ID") @PathVariable String itemId) {
        log.info("删除作品请求: itemId={}", itemId);
        heritageItemService.deleteHeritageItem(itemId);
        return Result.success();
    }

    /**
     * 发布作品
     */
    @Operation(summary = "发布作品", description = "将作品状态更改为已发布")
    @PostMapping("/{itemId}/publish")
    public Result<Void> publishHeritageItem(
            @Parameter(description = "作品ID") @PathVariable String itemId) {
        log.info("发布作品请求: itemId={}", itemId);
        heritageItemService.publishHeritageItem(itemId);
        return Result.success();
    }

    /**
     * 下架作品
     */
    @Operation(summary = "下架作品", description = "将作品状态更改为下架")
    @PostMapping("/{itemId}/offline")
    public Result<Void> offlineHeritageItem(
            @Parameter(description = "作品ID") @PathVariable String itemId) {
        log.info("下架作品请求: itemId={}", itemId);
        heritageItemService.offlineHeritageItem(itemId);
        return Result.success();
    }

    /**
     * 获取热门作品列表
     */
    @Operation(summary = "获取热门作品", description = "获取热门的非遗作品列表")
    @GetMapping("/popular")
    public Result<List<HeritageItemDetailResponseDTO>> getPopularItems(
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门作品请求: limit={}", limit);
        List<HeritageItemDetailResponseDTO> response = heritageItemService.getPopularItems(limit);
        return Result.success(response);
    }

    /**
     * 搜索作品
     */
    @Operation(summary = "搜索作品", description = "根据关键词搜索非遗作品")
    @GetMapping("/search")
    public Result<List<HeritageItemDetailResponseDTO>> searchItems(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "20") Integer limit) {
        log.info("搜索作品请求: keyword={}, limit={}", keyword, limit);
        List<HeritageItemDetailResponseDTO> response = heritageItemService.searchItems(keyword, limit);
        return Result.success(response);
    }

    /**
     * 获取最新发布的作品
     */
    @Operation(summary = "获取最新作品", description = "获取最新发布的非遗作品列表")
    @GetMapping("/latest")
    public Result<List<HeritageItemDetailResponseDTO>> getLatestItems(
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取最新作品请求: limit={}", limit);
        // 这里可以调用 mapper 的 selectLatestPublished 方法
        // 为了简化，暂时使用热门作品的逻辑
        List<HeritageItemDetailResponseDTO> response = heritageItemService.getPopularItems(limit);
        return Result.success(response);
    }

    // ========== 媒体管理接口已移除 ==========
    // 媒体文件现在直接通过 FileController 的文件上传API进行管理
    // 通过 business_type='HERITAGE_ITEM' 和 business_id=作品ID 进行关联
    // 媒体文件信息会在获取作品详情时一并返回
}
