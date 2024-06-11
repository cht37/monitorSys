package com.neu.monitorSys.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitorSys.user.DTO.GridManagerFullDTO;
import com.neu.monitorSys.user.entity.GridManager;
import com.baomidou.mybatisplus.extension.service.IService;

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
    IPage<GridManagerFullDTO> findGridManagersByConditions(GridManagerFullDTO gridManagerFullDTO, int page, int size);

    /**
     * 修改网格员信息
     */
    boolean updateGridManager(GridManager gridManager);
}
