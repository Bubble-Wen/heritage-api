package org.example.springboot.service.convert;

import org.example.springboot.dto.command.CourseChapterCreateCommandDTO;
import org.example.springboot.dto.command.CourseChapterUpdateCommandDTO;
import org.example.springboot.dto.response.CourseChapterResponseDTO;
import org.example.springboot.entity.CourseChapter;
import org.springframework.util.StringUtils;

/**
 * 课程章节DTO转换工具类
 * @author system
 */
public class CourseChapterConvert {

    /**
     * 创建命令DTO转实体
     */
    public static CourseChapter createCommandToEntity(CourseChapterCreateCommandDTO createDTO) {
        return CourseChapter.builder()
                .courseId(createDTO.getCourseId())
                .title(createDTO.getTitle())
                .content(createDTO.getContent())
                .sort(createDTO.getSort())
                .build();
    }

    /**
     * 实体转响应DTO
     * 注意：视频文件信息通过Service层查询文件系统获取
     */
    public static CourseChapterResponseDTO entityToResponse(CourseChapter chapter) {
        CourseChapterResponseDTO response = new CourseChapterResponseDTO();
        response.setId(chapter.getId());
        response.setCourseId(chapter.getCourseId());
        response.setTitle(chapter.getTitle());
        response.setContent(chapter.getContent());
        response.setSort(chapter.getSort());
        // videoFiles字段由Service层填充
        return response;
    }

    /**
     * 应用更新到实体
     */
    public static void applyUpdateToEntity(CourseChapter chapter, CourseChapterUpdateCommandDTO updateDTO) {
        if (StringUtils.hasText(updateDTO.getTitle())) {
            chapter.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getContent() != null) {
            chapter.setContent(updateDTO.getContent());
        }
        if (updateDTO.getSort() != null) {
            chapter.setSort(updateDTO.getSort());
        }
    }
}

