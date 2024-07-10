package com.neu.monitor_sys.geography.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitor_sys.common.entity.District;
import com.neu.monitor_sys.geography.mapper.DistrictMapper;
import com.neu.monitor_sys.geography.service.IDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistrictServiceImpl extends ServiceImpl<DistrictMapper,District> implements IDistrictService {
    @Autowired
    private DistrictMapper districtMapper;
    @Override
    public String getDistrictName(String districtId) {
        LambdaQueryWrapper<District> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(District::getDistrictId, districtId);
        District district = districtMapper.selectOne(wrapper);
        if (district == null) {
            return null;
        }
        return district.getDistrictName();
    }

    @Override
    public String getDistrictIdByCityId(String districtName, String cityId) {
        LambdaQueryWrapper<District> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(District::getCityId, cityId);
        wrapper.eq(District::getDistrictName, districtName);
        District district = districtMapper.selectOne(wrapper);
        if (district == null) {
            return null;
        }
        return district.getDistrictId();
    }


}
