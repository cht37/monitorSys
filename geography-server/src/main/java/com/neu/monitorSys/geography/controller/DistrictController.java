package com.neu.monitorSys.geography.controller;

import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.entity.constants.ResultCode;
import com.neu.monitorSys.geography.service.IDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p> 区县表 前端控制器 </p>
 */
@RestController
@RequestMapping("/api/v1/districts")
public class DistrictController {
    @Autowired
    private IDistrictService districtService;

    /**
     *
     * 查询区县名称
     * @param districtId
     * @return
     */
    @GetMapping("/{districtId}")
    public MyResponse<String> getDistrictName(@PathVariable String districtId) {
        String districtName = districtService.getDistrictName(districtId);
        if (districtName == null || districtName.equals("")) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", districtName);
    }

    /**
     * 通过城市id和区县名查询区县id
     * @param districtName
     * @param cityId
     * @return
     */
    @GetMapping("/id")
    public MyResponse<String> getDistrictId(@RequestParam String districtName, @RequestParam String cityId) {
        String districtId = districtService.getDistrictIdByCityId(districtName, cityId);
        if (districtId == null || districtId.equals("")) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", districtId);
    }
}
