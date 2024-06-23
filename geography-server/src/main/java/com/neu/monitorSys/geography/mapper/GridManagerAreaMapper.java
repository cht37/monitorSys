package com.neu.monitorSys.geography.mapper;

import com.neu.monitorSys.geography.DTO.GeographyVO;
import com.neu.monitorSys.entity.GridManagerArea;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
@Mapper
public interface GridManagerAreaMapper extends BaseMapper<GridManagerArea> {

    GeographyVO getAreDetailById(Integer id);
}
