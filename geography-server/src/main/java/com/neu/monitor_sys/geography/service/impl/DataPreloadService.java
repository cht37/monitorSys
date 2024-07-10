package com.neu.monitor_sys.geography.service.impl;

import com.neu.monitor_sys.common.entity.Cities;
import com.neu.monitor_sys.common.entity.Provinces;
import com.neu.monitor_sys.geography.constants.GeoRedisPrefix;
import com.neu.monitor_sys.geography.service.ICitiesService;
import com.neu.monitor_sys.geography.service.IProvincesService;
import com.neu.monitor_sys.geography.util.RedisUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据预加载服务
 */
@Service
public class DataPreloadService {
    @Autowired
    private IProvincesService provinceService;
    @Autowired
    private ICitiesService cityService;
    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void preloadData() {
        loadProvinces();
        loadCities();
    }

    private void loadProvinces() {
        //判断是否已经加载过
        if (redisUtil.hget(GeoRedisPrefix.PROVINCE_ID_TO_NAME, "1") != null) {
            return;
        }
        List<Provinces> provinces = provinceService.getAllProvinces();
        for (Provinces province : provinces) {
            // 存储ID到名称的映射
            redisUtil.hset(GeoRedisPrefix.PROVINCE_ID_TO_NAME , String.valueOf(province.getProvinceId()), province.getProvinceName());
            // 存储名称到ID的映射
            redisUtil.hset(GeoRedisPrefix.PROVINCE_NAME_TO_ID, province.getProvinceName(), String.valueOf(province.getProvinceId()));
        }
    }

    private void loadCities() {
        //判断是否已经加载过
        if (redisUtil.hget(GeoRedisPrefix.CITY_ID_TO_NAME, "1") != null) {
            return;
        }
        List<Cities> cities = cityService.getAllCities();
        for (Cities city : cities) {
             // 存储ID到名称的映射
            redisUtil.hset(GeoRedisPrefix.CITY_ID_TO_NAME, city.getCityId(), city.getCityName());
            // 存储名称到ID的映射
            redisUtil.hset(GeoRedisPrefix.CITY_NAME_TO_ID, city.getProvinceId()+city.getCityName(), String.valueOf(city.getCityId()));
        }
    }
}
