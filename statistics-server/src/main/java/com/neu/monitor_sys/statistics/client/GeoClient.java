package com.neu.monitor_sys.statistics.client;

import com.neu.monitor_sys.common.DTO.MyResponse;
import com.neu.monitor_sys.common.DTO.AreaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geography-server")
public interface GeoClient {
    /**
     * 根据城市名获取城市id
     */
    @GetMapping("/api/v1/cities/id")
    MyResponse getCityIdByProvinceId(@RequestParam("cityName") String cityName, @RequestParam("provinceId") String provinceId);
    /**
     * 根据省份名获取省份id
     */
    @GetMapping("/api/v1/provinces/id")
    MyResponse getProvinceId(@RequestParam("provinceName") String provinceName);
    /**
     * 写入管理区域信息
     */
    @GetMapping("/api/v1/area")
    MyResponse saveArea(@RequestBody AreaDTO areaDTO);
    /**
     * 根据省份id获取省份名
     */
    @GetMapping("/api/v1/provinces/{provinceId}")
    MyResponse getProvinceName(@PathVariable("provinceId") String provinceId);
    /**
     * 根据城市id获取城市名
     */
    @GetMapping("/api/v1/cities/{cityId}")
    MyResponse getCityName(@PathVariable("cityId") String cityId);
    /**
     * 根据区名，省份id获取区id
     */
    @GetMapping("/api/v1/districts/id")
    MyResponse getDistrictId(@RequestParam("districtName") String districtName, @RequestParam("cityId") String cityId);

    /**
     * 根据区id获取区名
     */
    @GetMapping("/api/v1/districts/{districtId}")
    MyResponse getDistrictName(@PathVariable("districtId") String districtId);
}
