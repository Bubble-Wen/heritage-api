package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.Course;

/**
 * 课程数据访问接口
 * @author system
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}


