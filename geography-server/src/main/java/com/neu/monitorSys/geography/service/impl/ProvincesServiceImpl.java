package com.neu.monitorSys.geography.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neu.monitorSys.geography.entity.Provinces;
import com.neu.monitorSys.geography.mapper.ProvincesMapper;
import com.neu.monitorSys.geography.service.IProvincesService;
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
public class ProvincesServiceImpl extends ServiceImpl<ProvincesMapper, Provinces> implements IProvincesService {
    @Autowired
    private ProvincesMapper provincesMapper;
    @Override
    public String getProvinceName(String provinceId) {
        return provincesMapper.selectById(provinceId).getProvinceName();
    }

    @Override
    public Integer getProvinceId(String provinceName) {
        LambdaQueryWrapper<Provinces> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Provinces::getProvinceName, provinceName);
        return provincesMapper.selectOne(wrapper).getProvinceId();
    }
}
