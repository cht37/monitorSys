package com.neu.monitor_sys.geography.service;

import com.neu.monitor_sys.geography.DTO.AreaMQDTO;
import com.neu.monitor_sys.geography.DTO.AreaDTO;
import com.neu.monitor_sys.geography.DTO.GeographyVO;
import com.neu.monitor_sys.common.entity.GridManagerArea;
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
    GeographyVO getAreDetailById(Integer id);

    /**
     * 写入管理区域信息
     */
    Boolean saveArea(AreaDTO areaDTO);

    /**
     * 通过MQ写入管理区域信息
     */
    Boolean saveAreaByMQ(AreaMQDTO area);
    /**
     * 通过网格名称（地址）获取网格id
     */
    Integer getGridIdByGridName(String gridName);

    /**
     * 通过网格地址模糊查询获取网格Id
     */
    Integer getGridIdByGridNameLike(String gridName);

}
