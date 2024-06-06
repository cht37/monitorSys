package com.neu.monitorSys.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitorSys.user.DTO.GridManagerDTO;
import com.neu.monitorSys.user.entity.GridManager;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网格员表 服务类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
public interface IGridManagerService extends IService<GridManager> {
    /**
     * 1.根据网格员memberId(logId)获取网格员信息
     */
    GridManager getGridManagerByLogId(String logId);

    /**
     * 多条件查询网格员信息
     */
    IPage<GridManagerDTO> findGridManagersByConditions(GridManagerDTO gridManagerDTO, int page, int size);
}
