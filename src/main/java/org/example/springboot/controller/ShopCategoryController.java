package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.ShopCategoryCreateDTO;
import org.example.springboot.dto.command.ShopCategoryUpdateDTO;
import org.example.springboot.dto.response.ShopCategoryResponseDTO;
import org.example.springboot.service.ShopCategoryService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品分类控制器
 * @author system
 */
@Tag(name = "商品分类管理")
@RestController
@RequestMapping("/shop/category")
@Slf4j
public class ShopCategoryController {

    @Resource
    private ShopCategoryService shopCategoryService;

    @Operation(summary = "分页查询分类列表")
    @GetMapping("/page")
    public Result<Page<ShopCategoryResponseDTO>> getCategoryPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "分类名称") @RequestParam(required = false) String name,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        log.info("分页查询商品分类列表");
        Page<ShopCategoryResponseDTO> result = shopCategoryService.getCategoryPage(current, size, name, status);
        return Result.success(result);
    }

    @Operation(summary = "获取所有分类列表")
    @GetMapping("/list")
    public Result<List<ShopCategoryResponseDTO>> getAllCategories() {
        log.info("获取所有商品分类列表");
        List<ShopCategoryResponseDTO> categories = shopCategoryService.getAllCategories();
        return Result.success(categories);
    }

    @Operation(summary = "获取启用的分类列表")
    @GetMapping("/list/enabled")
    public Result<List<ShopCategoryResponseDTO>> getEnabledCategories() {
        log.info("获取启用的商品分类列表");
        List<ShopCategoryResponseDTO> categories = shopCategoryService.getEnabledCategories();
        return Result.success(categories);
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<ShopCategoryResponseDTO> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("获取分类详情，ID: {}", id);
        ShopCategoryResponseDTO category = shopCategoryService.getCategoryById(id);
        return Result.success(category);
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<Void> createCategory(@Valid @RequestBody ShopCategoryCreateDTO dto) {
        log.info("创建商品分类: {}", dto);
        shopCategoryService.createCategory(dto);
        return Result.success();
    }

    @Operation(summary = "更新分类")
    @PutMapping
    public Result<Void> updateCategory(@Valid @RequestBody ShopCategoryUpdateDTO dto) {
        log.info("更新商品分类: {}", dto);
        shopCategoryService.updateCategory(dto);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("删除商品分类，ID: {}", id);
        shopCategoryService.deleteCategory(id);
        return Result.success();
    }

    @Operation(summary = "更新分类状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateCategoryStatus(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "状态 0禁用 1启用") @RequestParam Integer status) {
        log.info("更新分类状态，ID: {}, 状态: {}", id, status);
        shopCategoryService.updateCategoryStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "批量更新分类状态")
    @PutMapping("/batch/status")
    public Result<Void> batchUpdateStatus(
            @Parameter(description = "分类ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "状态 0禁用 1启用") @RequestParam Integer status) {
        log.info("批量更新分类状态，数量: {}, 状态: {}", ids.size(), status);
        shopCategoryService.batchUpdateStatus(ids, status);
        return Result.success();
    }
}