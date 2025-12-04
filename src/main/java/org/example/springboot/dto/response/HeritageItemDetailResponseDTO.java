package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 非遗作品详情响应DTO
 * @author system
 */
@Data
@Schema(description = "非遗作品详情响应")
public class HeritageItemDetailResponseDTO {

    @Schema(description = "作品ID")
    private String id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "类别")
    private String category;

    @Schema(description = "地区")
    private String region;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态码")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建人ID")
    private String creatorId;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "封面文件ID")
    private Long coverFileId;

    @Schema(description = "封面图片路径")
    private String coverImage;

    @Schema(description = "关联媒体文件列表")
    private List<HeritageItemMediaResponseDTO> mediaList;

    @Schema(description = "关联传承人列表")
    private List<HeritageItemInheritorResponseDTO> inheritorList;
}

