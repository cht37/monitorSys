package com.neu.monitor_sys.geography.controller;


import com.neu.monitor_sys.geography.DTO.AreaDTO;
import com.neu.monitor_sys.geography.DTO.GeographyVO;
import com.neu.monitor_sys.common.DTO.MyResponse;
import com.neu.monitor_sys.common.constants.ResultCode;
import com.neu.monitor_sys.geography.service.IGridManagerAreaService;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * grid_manager_area表前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
@RestController
@RequestMapping("/api/v1/areas")
public class GridManagerAreaController {
    @Autowired
    private IGridManagerAreaService gridManagerAreaService;
    /**
     * 根据Id获取管理区域信息
     */
    @GetMapping("/{id}")
    public MyResponse<GeographyVO> getAreDetailById(@PathVariable Integer id) {
        GeographyVO areDetailById = gridManagerAreaService.getAreDetailById(id);
        if (areDetailById != null) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", areDetailById);
        }
        return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "fail", new GeographyVO());
    }
    /**
     * 写入管理区域信息
     */
    @PostMapping
    public MyResponse<Null> saveArea(@RequestBody AreaDTO areaDTO) {
        Boolean saveArea = gridManagerAreaService.saveArea(areaDTO);
        if (saveArea) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", null);
        }
        return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "fail", null);
    }

    /**
     * 通过网格名称（地址）获取网格id
     * @param gridName
     * @return
     */
    @GetMapping("/grid-id")
    public MyResponse<Integer> getGridIdByGridName(@RequestParam String gridName) {
        Integer gridId = gridManagerAreaService.getGridIdByGridName(gridName);
        if (gridId != null) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", gridId);
        }
        return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "fail", null);
    }

    /**
     * 通过网格地址模糊查询获取网格Id
     * @param gridName
     * @return
     */
    @GetMapping("/grid-id-like")
    public MyResponse<Integer> getGridIdByGridNameLike(@RequestParam String gridName) {
        Integer gridId = gridManagerAreaService.getGridIdByGridNameLike(gridName);
        if (gridId != null) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", gridId);
        }
        return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "fail", null);
    }
}

