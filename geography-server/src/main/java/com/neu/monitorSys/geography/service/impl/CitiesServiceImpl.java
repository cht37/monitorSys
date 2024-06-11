package com.neu.monitorSys.geography.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neu.monitorSys.geography.entity.Cities;
import com.neu.monitorSys.geography.mapper.CitiesMapper;
import com.neu.monitorSys.geography.service.ICitiesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
@Service
public class CitiesServiceImpl extends ServiceImpl<CitiesMapper, Cities> implements ICitiesService {
    @Autowired
    private CitiesMapper citiesMapper;
    @Override
    public String getCityName(String cityId) {
        return citiesMapper.selectById(cityId).getCityName();
    }

    @Override
    public Integer getCityId(String cityName) {
        LambdaQueryWrapper<Cities> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cities::getCityName, cityName);
        return citiesMapper.selectOne(wrapper).getCityId();

    }
}
