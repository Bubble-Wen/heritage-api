package org.example.springboot.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品列表查询DTO
 * @author system
 */
@Data
@Schema(description = "商品列表查询")
public class ShopProductListQueryDTO {

    @Schema(description = "商品标题（模糊搜索）")
    private String title;

    @Schema(description = "类目ID")
    private Long categoryId;

    @Schema(description = "状态 0下架 1上架")
    private Integer status;

    @Schema(description = "最低价格")
    private java.math.BigDecimal minPrice;

    @Schema(description = "最高价格")
    private java.math.BigDecimal maxPrice;

    @Schema(description = "是否有库存（true:库存>0，false:库存=0）")
    private Boolean hasStock;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "排序字段（create_time/price/stock）", example = "create_time")
    private String sortField = "create_time";

    @Schema(description = "排序方式（asc/desc）", example = "desc")
    private String sortOrder = "desc";
}

