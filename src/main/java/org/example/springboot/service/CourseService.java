package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import org.example.springboot.entity.Course;
import org.example.springboot.entity.CourseChapter;
import org.example.springboot.entity.SysFileInfo;
import org.example.springboot.mapper.CourseMapper;
import org.example.springboot.mapper.CourseChapterMapper;
import org.example.springboot.mapper.SysFileInfoMapper;
import org.example.springboot.dto.command.CourseCreateCommandDTO;
import org.example.springboot.dto.command.CourseUpdateCommandDTO;
import org.example.springboot.dto.response.CourseDetailResponseDTO;
import org.example.springboot.dto.response.CourseResponseDTO;
import org.example.springboot.dto.response.CourseChapterResponseDTO;
import org.example.springboot.enums.CourseStatus;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.service.convert.CourseConvert;
import org.example.springboot.service.convert.CourseChapterConvert;

/**
 * 课程业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private CourseChapterMapper courseChapterMapper;

    @Resource
    private SysFileInfoMapper sysFileInfoMapper;

    @Resource
    private FileService fileService;

    /**
     * 创建课程
     * @param createDTO 创建命令
     * @return 课程详情
     */
    @Transactional(rollbackFor = Exception.class)
    public CourseDetailResponseDTO createCourse(CourseCreateCommandDTO createDTO) {
        try {
            // 验证UUID是否提供（策略B要求）
            if (!StringUtils.hasText(createDTO.getId())) {
                throw new BusinessException("课程ID不能为空，请使用UUID预生成策略");
            }

            // 验证UUID是否已存在
            Course existingCourse = courseMapper.selectById(createDTO.getId());
            if (existingCourse != null) {
                throw new BusinessException("课程ID已存在，请重新生成UUID");
            }

            // 创建课程实体
            Course course = CourseConvert.createCommandToEntity(createDTO);
            courseMapper.insert(course);

            log.info("课程创建成功: id={}, title={}", course.getId(), course.getTitle());

            // 查询并返回详情
            return getCourseById(course.getId());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("课程创建失败", e);
            throw new ServiceException("课程创建失败，请稍后重试");
        }
    }

    /**
     * 根据ID获取课程详情
     * @param courseId 课程ID
     * @return 课程详情
     */
    public CourseDetailResponseDTO getCourseById(String courseId) {
        try {
            Course course = courseMapper.selectById(courseId);
            if (course == null) {
                throw new BusinessException("课程不存在");
            }

            CourseDetailResponseDTO response = CourseConvert.entityToDetailResponse(course);

            // 查询封面文件路径
            if (course.getCoverFileId() != null) {
                SysFileInfo coverFile = sysFileInfoMapper.selectById(course.getCoverFileId());
                if (coverFile != null) {
                    response.setCoverFilePath(coverFile.getFilePath());
                }
            }

            // 查询章节列表
            LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseChapter::getCourseId, courseId)
                   .orderByAsc(CourseChapter::getSort);
            List<CourseChapter> chapters = courseChapterMapper.selectList(wrapper);

            List<CourseChapterResponseDTO> chapterDTOs = chapters.stream()
                    .map(chapter -> {
                        CourseChapterResponseDTO dto = CourseChapterConvert.entityToResponse(chapter);
                        // 通过文件系统关联查询视频文件
                        dto.setVideoFiles(fileService.getFilesByBusinessField(
                            "COURSE_CHAPTER", 
                            String.valueOf(chapter.getId()), 
                            "video"
                        ));
                        return dto;
                    })
                    .collect(Collectors.toList());

            response.setChapters(chapterDTOs);

            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取课程详情失败: courseId={}", courseId, e);
            throw new ServiceException("获取课程详情失败，请稍后重试");
        }
    }

    /**
     * 更新课程
     * @param courseId 课程ID
     * @param updateDTO 更新命令
     * @return 课程详情
     */
    @Transactional(rollbackFor = Exception.class)
    public CourseDetailResponseDTO updateCourse(String courseId, CourseUpdateCommandDTO updateDTO) {
        try {
            Course course = courseMapper.selectById(courseId);
            if (course == null) {
                throw new BusinessException("课程不存在");
            }

            // 验证状态转换是否合法
            if (updateDTO.getStatus() != null && !updateDTO.getStatus().equals(course.getStatus())) {
                if (!CourseStatus.isValidTransition(course.getStatus(), updateDTO.getStatus())) {
                    throw new BusinessException("状态转换不合法");
                }
            }

            // 应用更新
            CourseConvert.applyUpdateToEntity(course, updateDTO);
            courseMapper.updateById(course);

            log.info("课程更新成功: id={}", courseId);

            return getCourseById(courseId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("课程更新失败: courseId={}", courseId, e);
            throw new ServiceException("课程更新失败，请稍后重试");
        }
    }

    /**
     * 分页查询课程列表
     * @param current 当前页
     * @param size 每页大小
     * @param title 标题（模糊查询）
     * @param level 难度等级
     * @param status 状态
     * @return 分页结果
     */
    public Page<CourseResponseDTO> getCoursePage(Long current, Long size, String title, String level, Integer status) {
        try {
            Page<Course> page = new Page<>(current, size);
            LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();

            if (StrUtil.isNotBlank(title)) {
                wrapper.like(Course::getTitle, title);
            }
            if (StrUtil.isNotBlank(level)) {
                wrapper.eq(Course::getLevel, level);
            }
            if (status != null) {
                wrapper.eq(Course::getStatus, status);
            }

            // 按创建时间倒序
            wrapper.orderByDesc(Course::getCreateTime);

            Page<Course> coursePage = courseMapper.selectPage(page, wrapper);

            // 转换为响应DTO
            List<CourseResponseDTO> responseDTOList = coursePage.getRecords().stream()
                    .map(course -> {
                        CourseResponseDTO dto = CourseConvert.entityToResponse(course);
                        // 查询封面文件路径
                        if (course.getCoverFileId() != null) {
                            SysFileInfo coverFile = sysFileInfoMapper.selectById(course.getCoverFileId());
                            if (coverFile != null) {
                                dto.setCoverFilePath(coverFile.getFilePath());
                            }
                        }
                        // 查询章节数
                        LambdaQueryWrapper<CourseChapter> chapterWrapper = new LambdaQueryWrapper<>();
                        chapterWrapper.eq(CourseChapter::getCourseId, course.getId());
                        Long chapterCount = courseChapterMapper.selectCount(chapterWrapper);
                        dto.setChapterCount(chapterCount);
                        return dto;
                    })
                    .collect(Collectors.toList());

            Page<CourseResponseDTO> responsePage = new Page<>(coursePage.getCurrent(), coursePage.getSize(), coursePage.getTotal());
            responsePage.setRecords(responseDTOList);

            return responsePage;

        } catch (Exception e) {
            log.error("分页查询课程列表失败", e);
            throw new ServiceException("分页查询课程列表失败，请稍后重试");
        }
    }

    /**
     * 删除课程
     * @param courseId 课程ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourse(String courseId) {
        try {
            Course course = courseMapper.selectById(courseId);
            if (course == null) {
                throw new BusinessException("课程不存在");
            }

            // 检查是否有章节
            checkRelatedChapters(courseId);

            // 删除课程
            courseMapper.deleteById(courseId);

            log.info("课程删除成功: id={}", courseId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("课程删除失败: courseId={}", courseId, e);
            throw new ServiceException("课程删除失败，请稍后重试");
        }
    }

    /**
     * 检查关联的章节
     * @param courseId 课程ID
     */
    private void checkRelatedChapters(String courseId) {
        LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseChapter::getCourseId, courseId);
        Long count = courseChapterMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该课程存在章节，无法删除");
        }
    }

    /**
     * 获取最新课程列表
     * @param limit 限制数量
     * @return 课程列表
     */
    public List<CourseResponseDTO> getLatestCourses(Integer limit) {
        try {
            LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Course::getStatus, CourseStatus.PUBLISHED.getCode())
                    .orderByDesc(Course::getCreateTime)
                    .last("LIMIT " + limit);

            List<Course> courses = courseMapper.selectList(wrapper);

            return courses.stream()
                    .map(course -> {
                        CourseResponseDTO dto = CourseConvert.entityToResponse(course);
                        if (course.getCoverFileId() != null) {
                            SysFileInfo coverFile = sysFileInfoMapper.selectById(course.getCoverFileId());
                            if (coverFile != null) {
                                dto.setCoverFilePath(coverFile.getFilePath());
                            }
                        }
                        // 查询章节数
                        LambdaQueryWrapper<CourseChapter> chapterWrapper = new LambdaQueryWrapper<>();
                        chapterWrapper.eq(CourseChapter::getCourseId, course.getId());
                        Long chapterCount = courseChapterMapper.selectCount(chapterWrapper);
                        dto.setChapterCount(chapterCount);
                        return dto;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取最新课程列表失败", e);
            throw new ServiceException("获取最新课程列表失败，请稍后重试");
        }
    }
}

