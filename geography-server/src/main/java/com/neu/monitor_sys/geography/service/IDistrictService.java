package com.neu.monitor_sys.geography.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neu.monitor_sys.common.entity.District;

public interface IDistrictService extends IService<District> {
    /**
     * 根据区县ID获取区县名称
     * @param districtId
     * @return
     */
    String getDistrictName(String districtId);

    /**
     * 根据城市ID获取区县ID
     * @param districtName
     * @param cityId
     * @return
     */
    String getDistrictIdByCityId(String districtName,String cityId);
}
