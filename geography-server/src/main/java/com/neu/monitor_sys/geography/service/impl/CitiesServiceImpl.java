package com.neu.monitor_sys.geography.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitor_sys.common.entity.Cities;
import com.neu.monitor_sys.geography.constants.GeoRedisPrefix;
import com.neu.monitor_sys.geography.mapper.CitiesMapper;
import com.neu.monitor_sys.geography.service.ICitiesService;
import com.neu.monitor_sys.geography.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
@Service
public class CitiesServiceImpl extends ServiceImpl<CitiesMapper, Cities> implements ICitiesService {
    @Autowired
    private CitiesMapper citiesMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String getCityName(String cityId) {
        //从redis中获取
        String cityName = (String) redisUtil.hget(GeoRedisPrefix.CITY_ID_TO_NAME, cityId);
        if (cityName != null) {
            return cityName;
        }
        LambdaQueryWrapper<Cities> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cities::getCityId, cityId);
        Cities cities = citiesMapper.selectOne(wrapper);
        if (cities == null) {
            return null;
        }
        return cities.getCityName();
    }

    @Override
    public String getCityIdByProvinceId(String cityName, String provinceId) {
        //从redis中获取
        String cityId = (String) redisUtil.hget(GeoRedisPrefix.CITY_NAME_TO_ID, provinceId+ cityName);
        if (cityId != null) {
            return cityId;
        }
        LambdaQueryWrapper<Cities> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cities::getCityName, cityName);
        wrapper.eq(Cities::getProvinceId, provinceId);
        Cities cities = citiesMapper.selectOne(wrapper);
        if (cities == null) {
            return null;
        }
        return cities.getCityId();

    }

    @Override
    public List<Cities> getAllCities() {

        return citiesMapper.selectList(null);
    }
}
