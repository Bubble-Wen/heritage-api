package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.CourseChapter;

/**
 * 课程章节数据访问接口
 * @author system
 */
@Mapper
public interface CourseChapterMapper extends BaseMapper<CourseChapter> {
}


