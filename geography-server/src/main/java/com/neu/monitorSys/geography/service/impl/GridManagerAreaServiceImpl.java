package com.neu.monitorSys.geography.service.impl;

import com.neu.monitorSys.geography.DTO.GeographyDTO;
import com.neu.monitorSys.geography.entity.GridManagerArea;
import com.neu.monitorSys.geography.mapper.GridManagerAreaMapper;
import com.neu.monitorSys.geography.service.IGridManagerAreaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
@Service
public class GridManagerAreaServiceImpl extends ServiceImpl<GridManagerAreaMapper, GridManagerArea> implements IGridManagerAreaService {
    @Autowired
    private GridManagerAreaMapper gridManagerAreaMapper;
    @Override
    public GeographyDTO getAreDetailById(Integer id) {
        return gridManagerAreaMapper.getAreDetailById(id);
    }
}
