package com.neu.monitorSys.geography.service;

import com.neu.monitorSys.geography.entity.Cities;
import com.baomidou.mybatisplus.extension.service.IService;

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

    Integer getCityId(String cityName);
}
