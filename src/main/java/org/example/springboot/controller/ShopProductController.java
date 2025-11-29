package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.ShopProductCreateCommandDTO;
import org.example.springboot.dto.command.ShopProductUpdateCommandDTO;
import org.example.springboot.dto.query.ShopProductListQueryDTO;
import org.example.springboot.dto.response.ShopProductDetailResponseDTO;
import org.example.springboot.dto.response.ShopProductListResponseDTO;
import org.example.springboot.service.ShopProductService;
import org.example.springboot.service.ProductRecommendService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 * @author system
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/shop/product")
@Slf4j
public class ShopProductController {

    @Resource
    private ShopProductService shopProductService;

    @Resource
    private ProductRecommendService productRecommendService;

    @Operation(summary = "创建商品")
    @PostMapping
    public Result<ShopProductDetailResponseDTO> createProduct(@Valid @RequestBody ShopProductCreateCommandDTO dto) {
        log.info("创建商品: {}", dto.getTitle());
        ShopProductDetailResponseDTO result = shopProductService.createProduct(dto);
        return Result.success(result);
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{id}")
    public Result<ShopProductDetailResponseDTO> updateProduct(
            @Parameter(description = "商品ID") @PathVariable String id,
            @Valid @RequestBody ShopProductUpdateCommandDTO dto) {
        log.info("更新商品，ID: {}", id);
        ShopProductDetailResponseDTO result = shopProductService.updateProduct(id, dto);
        return Result.success(result);
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@Parameter(description = "商品ID") @PathVariable String id) {
        log.info("删除商品，ID: {}", id);
        shopProductService.deleteProduct(id);
        return Result.success();
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<ShopProductDetailResponseDTO> getProductDetail(@Parameter(description = "商品ID") @PathVariable String id) {
        log.info("获取商品详情，ID: {}", id);
        ShopProductDetailResponseDTO result = shopProductService.getProductDetailById(id);
        return Result.success(result);
    }

    @Operation(summary = "分页查询商品列表")
    @GetMapping("/page")
    public Result<Page<ShopProductListResponseDTO>> getProductPage(@Valid ShopProductListQueryDTO queryDTO) {
        log.info("分页查询商品列表，页码: {}, 大小: {}", queryDTO.getPage(), queryDTO.getPageSize());
        Page<ShopProductListResponseDTO> result = shopProductService.getProductPage(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "上架商品")
    @PutMapping("/{id}/on-shelf")
    public Result<Void> onShelfProduct(@Parameter(description = "商品ID") @PathVariable String id) {
        log.info("上架商品，ID: {}", id);
        shopProductService.onShelfProduct(id);
        return Result.success();
    }

    @Operation(summary = "下架商品")
    @PutMapping("/{id}/off-shelf")
    public Result<Void> offShelfProduct(@Parameter(description = "商品ID") @PathVariable String id) {
        log.info("下架商品，ID: {}", id);
        shopProductService.offShelfProduct(id);
        return Result.success();
    }

    @Operation(summary = "更新商品库存")
    @PutMapping("/{id}/stock")
    public Result<Void> updateStock(
            @Parameter(description = "商品ID") @PathVariable String id,
            @Parameter(description = "库存数量") @RequestParam Integer quantity) {
        log.info("更新商品库存，ID: {}, 数量: {}", id, quantity);
        shopProductService.updateStock(id, quantity);
        return Result.success();
    }

    @Operation(summary = "获取商品推荐（基于协同过滤）")
    @GetMapping("/{id}/recommendations")
    public Result<List<ShopProductListResponseDTO>> getRecommendations(
            @Parameter(description = "商品ID") @PathVariable String id,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "4") Integer limit) {
        log.info("获取商品推荐，商品ID: {}, 推荐数量: {}", id, limit);
        
        // 获取推荐商品ID列表
        List<String> recommendedProductIds = productRecommendService.getRecommendedProducts(id, limit);
        
        // 根据ID列表获取商品详情
        List<ShopProductListResponseDTO> recommendedProducts = shopProductService.getProductsByIds(recommendedProductIds);
        
        return Result.success(recommendedProducts);
    }
}

