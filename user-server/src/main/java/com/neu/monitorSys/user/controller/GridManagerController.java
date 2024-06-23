package com.neu.monitorSys.user.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitorSys.user.DTO.AssignDTO;
import com.neu.monitorSys.user.DTO.GridManagerFullVO;
import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.user.constants.ResultCode;
import com.neu.monitorSys.entity.GridManager;
import com.neu.monitorSys.user.service.IGridManagerService;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/api/v1/grid-managers")
public class GridManagerController {
    @Autowired
    private IGridManagerService gridManagerService;
    /**
     * 根据logId获取网格员信息
     *
     * @param logId
     * @return
     */
    @GetMapping("/info")
    public MyResponse<GridManager> getGridManagerInfo(@RequestHeader("logId") String logId){
        GridManager gridManager = gridManagerService.getGridManagerByLogId(logId);
        if (gridManager == null) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "网格员信息不存在", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "获取网格员信息成功", gridManager);
    }
    /**
     * 多条件查询网格员信息
     *
     * @param gridManagerFullVO
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/search")
    public MyResponse<IPage<GridManagerFullVO>> searchGridManagers(
            @RequestBody(required = false) GridManagerFullVO gridManagerFullVO,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (gridManagerFullVO == null) {
                gridManagerFullVO = new GridManagerFullVO();
            }
            IPage<GridManagerFullVO> gridManagerDTOIPage = gridManagerService.findGridManagersByConditions(gridManagerFullVO, page, size);
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", gridManagerDTOIPage);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "查询失败", null);
        }
    }

    /**
     * 编辑网格员信息
     * @param gridManager
     * @return
     */
    @PutMapping
    @Transactional
    public MyResponse<Null> editGridMember(@RequestBody GridManager gridManager){
        boolean result = gridManagerService.updateGridManager(gridManager);
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "编辑成功", null);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "编辑失败", null);
    }

    /**
     * 网格员是否可指派
     * @param logId
     * @return
     */
    @GetMapping("/assignable")
    public MyResponse<Boolean> isAssign(@RequestParam String logId){
        boolean result = gridManagerService.isAssign(logId);
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "可以指派", true);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "不可以指派", false);
    }

    /**
     * 网格员接受对某网格的指派
     * @param assignDTO 指派信息
     * @return
     */
    @PostMapping("/assign/accept")
    public MyResponse<Boolean> acceptAssign(@RequestBody AssignDTO assignDTO,@RequestHeader("logId") String logId){
        boolean result = false;
        try {
            result = gridManagerService.acceptAssign(assignDTO,logId );
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "指派失败"+e.getMessage(), false);
        }
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "指派成功", true);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "指派失败", false);
    }
}

