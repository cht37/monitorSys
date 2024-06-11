package com.neu.monitorSys.geography.service;

import com.neu.monitorSys.geography.DTO.GeographyDTO;
import com.neu.monitorSys.geography.entity.GridManagerArea;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
public interface IGridManagerAreaService extends IService<GridManagerArea> {
    /**
     * 根据Id获取管理区域信息
     */
    GeographyDTO getAreDetailById(Integer id);

}
