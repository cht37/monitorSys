package com.neu.monitorSys.user.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitorSys.user.DTO.GridManagerDTO;
import com.neu.monitorSys.user.DTO.MyResponse;
import com.neu.monitorSys.user.constants.ResultCode;
import com.neu.monitorSys.user.entity.GridManager;
import com.neu.monitorSys.user.service.IGridManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 网格员表 前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
@RestController
@RequestMapping("/gridManager")
public class GridManagerController {
    @Autowired
    private IGridManagerService gridManagerService;
    /**
     * 根据logId获取网格员信息
     *
     * @param logId
     * @return
     */
    @GetMapping("/getGridManagerInfo/{logId}")
    public MyResponse<GridManager> getGridManagerInfo(@PathVariable String logId){
        GridManager gridManager = gridManagerService.getGridManagerByLogId(logId);
        if (gridManager == null) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "网格员信息不存在", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "获取网格员信息成功", gridManager);
    }
    /**
     * 多条件查询网格员信息
     *
     * @param gridManagerDTO
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/search")
    public MyResponse<IPage<GridManagerDTO>> searchGridManagers(
            @RequestBody GridManagerDTO gridManagerDTO,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            IPage<GridManagerDTO> gridManagerDTOIPage = gridManagerService.findGridManagersByConditions(gridManagerDTO, page, size);
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", gridManagerDTOIPage);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "查询失败", null);
        }
    }

}

