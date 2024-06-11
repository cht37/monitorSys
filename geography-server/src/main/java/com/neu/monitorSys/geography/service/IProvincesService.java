package com.neu.monitorSys.geography.service;

import com.neu.monitorSys.geography.entity.Provinces;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
public interface IProvincesService extends IService<Provinces> {
    /**
     * 获取省份名称
     */
    String getProvinceName(String provinceId);

    /**
     * 获取省份id
     */
    Integer getProvinceId(String provinceName);
}
