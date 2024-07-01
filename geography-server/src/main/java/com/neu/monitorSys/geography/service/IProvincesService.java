package com.neu.monitorSys.geography.service;

import com.neu.monitorSys.common.entity.Provinces;
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
public interface IProvincesService extends IService<Provinces> {
    /**
     * 获取省份名称
     */
    String getProvinceName(String provinceId);

    /**
     * 获取省份id
     */
    String getProvinceId(String provinceName);
    /**
     * 获取所有省份
     */
    List<Provinces> getAllProvinces();
}
