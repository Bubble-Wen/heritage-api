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

import org.example.springboot.common.Result;
import org.example.springboot.dto.command.CourseCreateCommandDTO;
import org.example.springboot.dto.command.CourseUpdateCommandDTO;
import org.example.springboot.dto.command.CourseChapterCreateCommandDTO;
import org.example.springboot.dto.command.CourseChapterUpdateCommandDTO;
import org.example.springboot.dto.response.CourseDetailResponseDTO;
import org.example.springboot.dto.response.CourseResponseDTO;
import org.example.springboot.dto.response.CourseChapterResponseDTO;
import org.example.springboot.service.CourseService;
import org.example.springboot.service.CourseChapterService;

import java.util.List;

/**
 * 课程管理控制器
 * @author system
 */
@Tag(name = "课程管理", description = "课程的增删改查、章节管理等操作")
@RequestMapping("/course")
@RestController
@Slf4j
@Validated
public class CourseController {

    @Resource
    private CourseService courseService;

    @Resource
    private CourseChapterService courseChapterService;

    /**
     * 创建课程
     */
    @Operation(summary = "创建课程", description = "创建新的课程")
    @PostMapping("/create")
    public Result<CourseDetailResponseDTO> createCourse(
            @Valid @RequestBody CourseCreateCommandDTO createDTO) {
        log.info("创建课程请求: title={}, level={}", createDTO.getTitle(), createDTO.getLevel());
        CourseDetailResponseDTO response = courseService.createCourse(createDTO);
        return Result.success(response);
    }

    /**
     * 根据ID获取课程详情
     */
    @Operation(summary = "获取课程详情", description = "根据课程ID获取详细信息")
    @GetMapping("/{courseId}")
    public Result<CourseDetailResponseDTO> getCourseById(
            @Parameter(description = "课程ID") @PathVariable String courseId) {
        log.info("获取课程详情请求: courseId={}", courseId);
        CourseDetailResponseDTO response = courseService.getCourseById(courseId);
        return Result.success(response);
    }

    /**
     * 分页查询课程列表
     */
    @Operation(summary = "分页查询课程列表", description = "根据条件分页查询课程列表")
    @GetMapping("/page")
    public Result<Page<CourseResponseDTO>> getCoursePage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "标题关键词") @RequestParam(required = false) String title,
            @Parameter(description = "难度等级") @RequestParam(required = false) String level,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        log.info("分页查询课程列表: page={}, size={}, title={}", current, size, title);

        Page<CourseResponseDTO> response = courseService.getCoursePage(current, size, title, level, status);
        return Result.success(response);
    }

    /**
     * 更新课程
     */
    @Operation(summary = "更新课程", description = "更新课程信息")
    @PutMapping("/{courseId}")
    public Result<CourseDetailResponseDTO> updateCourse(
            @Parameter(description = "课程ID") @PathVariable String courseId,
            @Valid @RequestBody CourseUpdateCommandDTO updateDTO) {
        log.info("更新课程请求: courseId={}", courseId);
        CourseDetailResponseDTO response = courseService.updateCourse(courseId, updateDTO);
        return Result.success(response);
    }

    /**
     * 删除课程
     */
    @Operation(summary = "删除课程", description = "删除课程")
    @DeleteMapping("/{courseId}")
    public Result<Void> deleteCourse(
            @Parameter(description = "课程ID") @PathVariable String courseId) {
        log.info("删除课程请求: courseId={}", courseId);
        courseService.deleteCourse(courseId);
        return Result.success();
    }

    /**
     * 获取最新课程列表
     */
    @Operation(summary = "获取最新课程列表", description = "获取最新发布的课程列表")
    @GetMapping("/latest")
    public Result<List<CourseResponseDTO>> getLatestCourses(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取最新课程列表请求: limit={}", limit);
        List<CourseResponseDTO> response = courseService.getLatestCourses(limit);
        return Result.success(response);
    }

    /**
     * 创建课程章节
     */
    @Operation(summary = "创建课程章节", description = "为课程添加新章节")
    @PostMapping("/chapter/create")
    public Result<CourseChapterResponseDTO> createChapter(
            @Valid @RequestBody CourseChapterCreateCommandDTO createDTO) {
        log.info("创建课程章节请求: courseId={}, title={}", createDTO.getCourseId(), createDTO.getTitle());
        CourseChapterResponseDTO response = courseChapterService.createChapter(createDTO);
        return Result.success(response);
    }

    /**
     * 获取章节详情
     */
    @Operation(summary = "获取章节详情", description = "根据章节ID获取详细信息")
    @GetMapping("/chapter/{chapterId}")
    public Result<CourseChapterResponseDTO> getChapterById(
            @Parameter(description = "章节ID") @PathVariable Long chapterId) {
        log.info("获取章节详情请求: chapterId={}", chapterId);
        CourseChapterResponseDTO response = courseChapterService.getChapterById(chapterId);
        return Result.success(response);
    }

    /**
     * 获取课程的章节列表
     */
    @Operation(summary = "获取课程章节列表", description = "获取指定课程的所有章节")
    @GetMapping("/{courseId}/chapters")
    public Result<List<CourseChapterResponseDTO>> getChaptersByCourseId(
            @Parameter(description = "课程ID") @PathVariable String courseId) {
        log.info("获取课程章节列表请求: courseId={}", courseId);
        List<CourseChapterResponseDTO> response = courseChapterService.getChaptersByCourseId(courseId);
        return Result.success(response);
    }

    /**
     * 更新章节
     */
    @Operation(summary = "更新章节", description = "更新章节信息")
    @PutMapping("/chapter/{chapterId}")
    public Result<CourseChapterResponseDTO> updateChapter(
            @Parameter(description = "章节ID") @PathVariable Long chapterId,
            @Valid @RequestBody CourseChapterUpdateCommandDTO updateDTO) {
        log.info("更新章节请求: chapterId={}", chapterId);
        CourseChapterResponseDTO response = courseChapterService.updateChapter(chapterId, updateDTO);
        return Result.success(response);
    }

    /**
     * 删除章节
     */
    @Operation(summary = "删除章节", description = "删除课程章节")
    @DeleteMapping("/chapter/{chapterId}")
    public Result<Void> deleteChapter(
            @Parameter(description = "章节ID") @PathVariable Long chapterId) {
        log.info("删除章节请求: chapterId={}", chapterId);
        courseChapterService.deleteChapter(chapterId);
        return Result.success();
    }
}


