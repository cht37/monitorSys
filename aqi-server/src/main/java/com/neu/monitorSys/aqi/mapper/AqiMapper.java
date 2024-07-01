package com.neu.monitorSys.aqi.mapper;

import com.neu.monitorSys.common.entity.Aqi;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 空气质量指数级别表 Mapper 接口
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-24
 */
@Mapper
public interface AqiMapper extends BaseMapper<Aqi> {

}
