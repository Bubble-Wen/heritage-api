package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.ActivitySignup;

/**
 * 活动报名数据访问接口
 * @author system
 */
@Mapper
public interface ActivitySignupMapper extends BaseMapper<ActivitySignup> {
}


