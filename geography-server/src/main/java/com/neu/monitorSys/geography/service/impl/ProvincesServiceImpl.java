package com.neu.monitorSys.geography.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neu.monitorSys.common.entity.Provinces;
import com.neu.monitorSys.geography.constants.GeoRedisPrefix;
import com.neu.monitorSys.geography.mapper.ProvincesMapper;
import com.neu.monitorSys.geography.service.IProvincesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.geography.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
@Service
public class ProvincesServiceImpl extends ServiceImpl<ProvincesMapper, Provinces> implements IProvincesService {
    @Autowired
    private ProvincesMapper provincesMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public String getProvinceName(String provinceId) {
        //从redis中获取
        String provinceName = (String) redisUtil.hget(GeoRedisPrefix.PROVINCE_ID_TO_NAME, provinceId);
        if (provinceName != null) {
            return provinceName;
        }
        LambdaQueryWrapper<Provinces> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Provinces::getProvinceId, provinceId);
        return provincesMapper.selectOne(wrapper).getProvinceName();
    }

    @Override
    public String getProvinceId(String provinceName) {
        //从redis中获取
        String provinceId = (String) redisUtil.hget(GeoRedisPrefix.PROVINCE_NAME_TO_ID, provinceName);
        if (provinceId != null) {
            return provinceId;
        }
        LambdaQueryWrapper<Provinces> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Provinces::getProvinceName, provinceName);
        return provincesMapper.selectOne(wrapper).getProvinceId();
    }

    @Override
    public List<Provinces> getAllProvinces() {
        return provincesMapper.selectList(null);
    }
}
