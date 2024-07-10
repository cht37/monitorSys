package com.neu.monitor_sys.geography.service;

import com.neu.monitor_sys.common.entity.Cities;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
public interface ICitiesService extends IService<Cities> {
    String getCityName(String cityId);

    String getCityIdByProvinceId(String cityName,String provinceId);

    /**
     * 获取所有城市
     */
    List<Cities> getAllCities();
}
