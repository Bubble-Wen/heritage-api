package org.example.springboot.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 非遗作品列表查询DTO
 * @author system
 */
@Data
@Schema(description = "非遗作品列表查询")
public class HeritageItemListQueryDTO {

    @Schema(description = "当前页", example = "1")
    private Integer currentPage = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "标题关键词")
    private String title;

    @Schema(description = "类别")
    private String category;

    @Schema(description = "地区")
    private String region;

    @Schema(description = "状态 0草稿 1待审 2已发布 3下架")
    private Integer status;

    @Schema(description = "创建人ID")
    private String creatorId;

    @Schema(description = "开始时间(格式: yyyy-MM-dd)")
    private String startDate;

    @Schema(description = "结束时间(格式: yyyy-MM-dd)")
    private String endDate;

    @Schema(description = "排序字段", example = "create_time")
    private String orderBy = "create_time";

    @Schema(description = "排序方向", example = "desc")
    private String orderDirection = "desc";
}

