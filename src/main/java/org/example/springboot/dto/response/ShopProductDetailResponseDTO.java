package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情响应DTO
 * @author system
 */
@Data
@Schema(description = "商品详情响应")
public class ShopProductDetailResponseDTO {

    @Schema(description = "商品ID")
    private String id;

    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "类目ID")
    private Long categoryId;

    @Schema(description = "类目名称")
    private String categoryName;

    @Schema(description = "商品价格")
    private BigDecimal price;

    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "封面文件ID")
    private Long coverFileId;

    @Schema(description = "封面文件路径")
    private String coverFilePath;

    @Schema(description = "商品详情")
    private String detail;

    @Schema(description = "状态码 0下架 1上架")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "商品图片列表")
    private List<ShopProductImageResponseDTO> imageList;
}

