package com.neu.monitorSys.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitorSys.user.DTO.AssignDTO;
import com.neu.monitorSys.user.DTO.GridManagerDTO;
import com.neu.monitorSys.user.DTO.GridManagerFullVO;
import com.neu.monitorSys.common.entity.GridManager;
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
    IPage<GridManagerFullVO> findGridManagersByConditions(GridManagerDTO gridManagerDTO, int page, int size);

    /**
     * 修改网格员信息
     */
    boolean updateGridManager(GridManager gridManager);
    /**
     * 网格员是否可指派
     */
    boolean isAssign(String logId);

    /**
     * 网格员是否已经被指派给afId反馈
     * @param logId 网格员id
     * @param afId 反馈id
     * @return 是否可指派
     */
    boolean isAssign(String logId, Integer afId);

    /**
     * 网格员接受对某网格的指派
     */
    boolean acceptAssign(AssignDTO assignDTO,String logId);

    /**
     * 网格员是否正在处理反馈
     */
    boolean isProcessing(String logId);

    /**
     * 根据logId获取AfId
     */
    Integer getAfIdByLogId(String logId);

    /**
     * 修改网格员状态
     */
    boolean updateState(String logId, Integer state);

}
