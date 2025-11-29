package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import org.example.springboot.entity.Course;
import org.example.springboot.entity.CourseChapter;
import org.example.springboot.mapper.CourseMapper;
import org.example.springboot.mapper.CourseChapterMapper;
import org.example.springboot.dto.command.CourseChapterCreateCommandDTO;
import org.example.springboot.dto.command.CourseChapterUpdateCommandDTO;
import org.example.springboot.dto.response.CourseChapterResponseDTO;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.service.convert.CourseChapterConvert;

/**
 * 课程章节业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class CourseChapterService {

    @Resource
    private CourseChapterMapper courseChapterMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private FileService fileService;

    /**
     * 创建章节
     * @param createDTO 创建命令
     * @return 章节详情
     */
    @Transactional(rollbackFor = Exception.class)
    public CourseChapterResponseDTO createChapter(CourseChapterCreateCommandDTO createDTO) {
        try {
            // 验证课程是否存在
            Course course = courseMapper.selectById(createDTO.getCourseId());
            if (course == null) {
                throw new BusinessException("课程不存在");
            }

            // 验证排序号是否重复
            LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseChapter::getCourseId, createDTO.getCourseId())
                   .eq(CourseChapter::getSort, createDTO.getSort());
            CourseChapter existingChapter = courseChapterMapper.selectOne(wrapper);
            if (existingChapter != null) {
                throw new BusinessException("该排序号已存在，请使用其他排序号");
            }

            // 创建章节实体
            CourseChapter chapter = CourseChapterConvert.createCommandToEntity(createDTO);
            courseChapterMapper.insert(chapter);

            log.info("课程章节创建成功: id={}, courseId={}, title={}", chapter.getId(), chapter.getCourseId(), chapter.getTitle());

            // 查询并返回详情
            return getChapterById(chapter.getId());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("课程章节创建失败", e);
            throw new ServiceException("课程章节创建失败，请稍后重试");
        }
    }

    /**
     * 根据ID获取章节详情
     * @param chapterId 章节ID
     * @return 章节详情
     */
    public CourseChapterResponseDTO getChapterById(Long chapterId) {
        try {
            CourseChapter chapter = courseChapterMapper.selectById(chapterId);
            if (chapter == null) {
                throw new BusinessException("章节不存在");
            }

            CourseChapterResponseDTO response = CourseChapterConvert.entityToResponse(chapter);

            // 通过文件系统关联查询视频文件
            // businessType = "COURSE_CHAPTER", businessId = chapterId, businessField = "video"
            response.setVideoFiles(fileService.getFilesByBusinessField(
                "COURSE_CHAPTER", 
                String.valueOf(chapterId), 
                "video"
            ));

            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取章节详情失败: chapterId={}", chapterId, e);
            throw new ServiceException("获取章节详情失败，请稍后重试");
        }
    }

    /**
     * 更新章节
     * @param chapterId 章节ID
     * @param updateDTO 更新命令
     * @return 章节详情
     */
    @Transactional(rollbackFor = Exception.class)
    public CourseChapterResponseDTO updateChapter(Long chapterId, CourseChapterUpdateCommandDTO updateDTO) {
        try {
            CourseChapter chapter = courseChapterMapper.selectById(chapterId);
            if (chapter == null) {
                throw new BusinessException("章节不存在");
            }

            // 如果更新排序号，验证是否重复
            if (updateDTO.getSort() != null && !updateDTO.getSort().equals(chapter.getSort())) {
                LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(CourseChapter::getCourseId, chapter.getCourseId())
                       .eq(CourseChapter::getSort, updateDTO.getSort())
                       .ne(CourseChapter::getId, chapterId);
                CourseChapter existingChapter = courseChapterMapper.selectOne(wrapper);
                if (existingChapter != null) {
                    throw new BusinessException("该排序号已存在，请使用其他排序号");
                }
            }

            // 应用更新
            CourseChapterConvert.applyUpdateToEntity(chapter, updateDTO);
            courseChapterMapper.updateById(chapter);

            log.info("课程章节更新成功: id={}", chapterId);

            return getChapterById(chapterId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("课程章节更新失败: chapterId={}", chapterId, e);
            throw new ServiceException("课程章节更新失败，请稍后重试");
        }
    }

    /**
     * 删除章节
     * @param chapterId 章节ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteChapter(Long chapterId) {
        try {
            CourseChapter chapter = courseChapterMapper.selectById(chapterId);
            if (chapter == null) {
                throw new BusinessException("章节不存在");
            }

            // 删除章节
            courseChapterMapper.deleteById(chapterId);

            log.info("课程章节删除成功: id={}", chapterId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("课程章节删除失败: chapterId={}", chapterId, e);
            throw new ServiceException("课程章节删除失败，请稍后重试");
        }
    }

    /**
     * 获取课程的章节列表
     * @param courseId 课程ID
     * @return 章节列表
     */
    public List<CourseChapterResponseDTO> getChaptersByCourseId(String courseId) {
        try {
            // 验证课程是否存在
            Course course = courseMapper.selectById(courseId);
            if (course == null) {
                throw new BusinessException("课程不存在");
            }

            LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseChapter::getCourseId, courseId)
                   .orderByAsc(CourseChapter::getSort);

            List<CourseChapter> chapters = courseChapterMapper.selectList(wrapper);

            return chapters.stream()
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

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取课程章节列表失败: courseId={}", courseId, e);
            throw new ServiceException("获取课程章节列表失败，请稍后重试");
        }
    }

    /**
     * 批量删除课程的所有章节
     * @param courseId 课程ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteChaptersByCourseId(String courseId) {
        try {
            LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseChapter::getCourseId, courseId);
            courseChapterMapper.delete(wrapper);

            log.info("批量删除课程章节成功: courseId={}", courseId);

        } catch (Exception e) {
            log.error("批量删除课程章节失败: courseId={}", courseId, e);
            throw new ServiceException("批量删除课程章节失败，请稍后重试");
        }
    }
}

