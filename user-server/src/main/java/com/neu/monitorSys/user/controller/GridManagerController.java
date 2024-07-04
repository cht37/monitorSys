package com.neu.monitorSys.user.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitorSys.common.DTO.MyResponse;
import com.neu.monitorSys.common.entity.GridManager;
import com.neu.monitorSys.user.DTO.AssignDTO;
import com.neu.monitorSys.user.DTO.GridManagerDTO;
import com.neu.monitorSys.user.DTO.GridManagerFullVO;
import com.neu.monitorSys.user.constants.ResultCode;
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
    public MyResponse<GridManager> getGridManagerInfo(@RequestHeader("logId") String logId) {
        GridManager gridManager = gridManagerService.getGridManagerByLogId(logId);
        if (gridManager == null) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "网格员信息不存在", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "获取网格员信息成功", gridManager);
    }

    /**
     * 多条件查询网格员信息
     *
     * @param gridManagerFullVO 查询条件
     * @param page              页数
     * @param size              每页大小
     * @return 网格员信息（分页）
     */
    @GetMapping("/search")
    public MyResponse<IPage<GridManagerFullVO>> searchGridManagers(
            @ModelAttribute GridManagerDTO gridManagerDTO,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            IPage<GridManagerFullVO> gridManagerDTOIPage = gridManagerService.findGridManagersByConditions(gridManagerDTO, page, size);
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", gridManagerDTOIPage);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "查询失败", null);
        }
    }

    /**
     * 编辑网格员信息
     *
     * @param gridManager
     * @return
     */
    @PutMapping
    @Transactional
    public MyResponse<Null> editGridMember(@RequestBody GridManager gridManager) {
        boolean result = gridManagerService.updateGridManager(gridManager);
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "编辑成功", null);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "编辑失败", null);
    }

    /**
     * 网格员是否可指派
     *
     * @param logId
     * @return
     */
    @GetMapping("/assignable")
    public MyResponse<Boolean> isAssign(@RequestParam String logId) {
        boolean result = gridManagerService.isAssign(logId);
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "可以指派", true);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "不可以指派", false);
    }

    /**
     * 网格员接受对某网格的指派
     *
     * @param assignDTO 指派信息
     * @return
     */
    @PostMapping("/assign/accept")
    public MyResponse<Boolean> acceptAssign(@RequestBody AssignDTO assignDTO, @RequestHeader("logId") String logId) {
        boolean result = false;
        try {
            result = gridManagerService.acceptAssign(assignDTO, logId);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "指派失败" + e.getMessage(), false);
        }
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "指派成功", true);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "指派失败", false);
    }

    /**
     * 网格员是否可指派或是否已经指派给afId反馈
     *
     * @param logId 网格员id
     * @return 是否可指派
     */
    @GetMapping("/available")
    public MyResponse<Boolean> isAvailable(@RequestHeader("logId") String logId) {
        boolean result = false;
        try {
            result = gridManagerService.isProcessing(logId);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "不可用"+e.getMessage(), false);
        }
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "可用", true);
        } else {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "不可用", false);
        }
    }

    /**
     * 根据logId获取AfId
     *
     * @param logId 网格员id
     * @return afId
     */
    @GetMapping("/afId")
    public MyResponse<Integer> getAfIdByLogId(@RequestParam String logId) {
        Integer afId = gridManagerService.getAfIdByLogId(logId);
        if (afId == null) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "查询失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", afId);
    }

    /**
     * 修改网格员状态
     * @param logId 网格员id
     * @param state 状态
     * @return 是否修改成功
     */
    @PutMapping("/state")
    public MyResponse<Boolean> updateState(@RequestParam String logId, @RequestParam Integer state) {
        boolean result = false;
        try {
            result = gridManagerService.updateState(logId, state);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "修改失败" + e.getMessage(), false);
        }
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "修改成功", true);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "修改失败", false);
    }

    /**
     * 新增网格员记录
     * @param logId 网格员id
     * @return 是否新增成功
     */
    @PostMapping
    public MyResponse<Boolean> addGridManager(@RequestParam String logId) {
        boolean result = false;
        try {
            result = gridManagerService.addGridManager(logId);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "新增失败" + e.getMessage(), false);
        }
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "新增成功", true);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "新增失败", false);
    }

    /**
     * 删除网格员记录（逻辑）
     * @param logId 网格员id
     * @return 是否删除成功
     */
    @DeleteMapping
    public MyResponse<Boolean> deleteGridManager(@RequestParam String logId) {
        boolean result = false;
        try {
            result = gridManagerService.deleteGridManager(logId);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "删除失败" + e.getMessage(), false);
        }
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "删除成功", true);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "删除失败", false);
    }
}

