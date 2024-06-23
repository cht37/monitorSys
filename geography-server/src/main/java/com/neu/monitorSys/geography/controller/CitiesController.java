package com.neu.monitorSys.geography.controller;


import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.entity.constants.ResultCode;
import com.neu.monitorSys.geography.service.ICitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  城市表 前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
@RestController
@RequestMapping("/api/v1/cities")
public class CitiesController {
    @Autowired
    private ICitiesService citiesService;
    /**
     * 1. 根据城市id获取城市名称
     */
    @GetMapping("/{cityId}")
    public MyResponse<String> getCityName(@PathVariable String cityId) {
        String cityName = citiesService.getCityName(cityId);
        if (cityName == null||cityName.equals("")) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功",cityName);
    }
    /**
     * 2. 根据城市名称获取城市id
     */
    @GetMapping("/id")
    public MyResponse<String> getCityId(@RequestParam String cityName,@RequestParam String provinceId) {
        String cityId = citiesService.getCityIdByProvinceId(cityName,provinceId);
        if (cityId == null) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功",cityId);
    }

}

