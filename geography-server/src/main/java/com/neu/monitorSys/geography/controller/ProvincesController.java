package com.neu.monitorSys.geography.controller;


import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.entity.constants.ResultCode;
import com.neu.monitorSys.geography.service.IProvincesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  provinces表前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
@RestController
@RequestMapping("/api/v1/provinces")
public class ProvincesController {
    @Autowired
    private IProvincesService provincesService;

    /**
     * 1. 根据省份id获取省份名称
     */
    @GetMapping("/{provinceId}")
    public MyResponse<String> getProvinceName(@PathVariable String provinceId) {
        String provinceName = provincesService.getProvinceName(provinceId);
        if (provinceName == null||provinceName.equals("")) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功",provincesService.getProvinceName(provinceId));
    }
    /**
     * 2. 根据省份名称获取省份id
     */
    @GetMapping("/id")
    public MyResponse<String> getProvinceId(@RequestParam String provinceName) {
        String provinceId = provincesService.getProvinceId(provinceName);
        if (provinceId == null) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功",provinceId);
    }
}

