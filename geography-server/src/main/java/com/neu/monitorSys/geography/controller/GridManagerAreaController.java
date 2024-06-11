package com.neu.monitorSys.geography.controller;


import com.neu.monitorSys.geography.DTO.GeographyDTO;
import com.neu.monitorSys.geography.DTO.MyResponse;
import com.neu.monitorSys.geography.constants.ResultCode;
import com.neu.monitorSys.geography.entity.GridManagerArea;
import com.neu.monitorSys.geography.service.IGridManagerAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * grid_manager_area表前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
@RestController
@RequestMapping("/area")
public class GridManagerAreaController {
    @Autowired
    private IGridManagerAreaService gridManagerAreaService;
    /**
     * 根据Id获取管理区域信息
     */
    @GetMapping("/getGeoInfo/{id}")
    public MyResponse<GeographyDTO> getAreDetailById(@PathVariable Integer id) {
        GeographyDTO areDetailById = gridManagerAreaService.getAreDetailById(id);
        if (areDetailById != null) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", areDetailById);
        }
        return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "fail", new GeographyDTO());
    }
}

