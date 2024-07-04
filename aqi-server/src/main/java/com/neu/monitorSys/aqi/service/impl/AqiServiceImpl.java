package com.neu.monitorSys.aqi.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.aqi.mapper.AqiMapper;
import com.neu.monitorSys.aqi.service.IAqiService;
import com.neu.monitorSys.aqi.util.RedisUtil;
import com.neu.monitorSys.common.DTO.AqiDTO;
import com.neu.monitorSys.common.constants.RedisPrefix;
import com.neu.monitorSys.common.entity.Aqi;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 空气质量指数级别表 服务实现类
 * </p>
 *
 * @since 2024-06-24
 */
@Service
public class AqiServiceImpl extends ServiceImpl<AqiMapper, Aqi> implements IAqiService {
    @Autowired
    private AqiMapper aqiMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Aqi> getAllAqi() {
        // 查询redis缓存
        Object o = redisUtil.get(RedisPrefix.AQI_DETAIL);
        if (o != null) {
            String string = o.toString();
            return JSONUtil.toList(string, Aqi.class);
        }
        List<Aqi> aqis = aqiMapper.selectList(null);
        if (aqis != null) {
            // 存入redis缓存，缓存时间为3小时
            redisUtil.set(RedisPrefix.AQI_DETAIL, JSONUtil.toJsonStr(aqis), 3 * 60 * 60);
        }
        return aqis;
    }

    /**
     * 计算aqi
     *
     * @param so2 二氧化硫浓度
     * @param co  一氧化碳浓度
     * @param spm 悬浮颗粒物浓度
     * @return 综合AQI值
     */
    @Override
    public int[] calculateAqi(int so2, int co, int spm) {
        List<Aqi> allAqi = getAllAqi();
        int aqiSO2 = calculateIndividualAqi(so2, allAqi, "SO2");
        int aqiCO = calculateIndividualAqi(co, allAqi, "CO");
        int aqiSPM = calculateIndividualAqi(spm, allAqi, "SPM");
        return new int[]{aqiSO2, aqiCO, aqiSPM, Math.max(aqiSO2, Math.max(aqiCO, aqiSPM))};
    }

    /**
     * 获取aqi级别
     *
     * @param value aqi值
     * @return aqi级别
     */
    @Override
    public AqiDTO getAqiLevel(int value) {
        List<Aqi> allAqi = getAllAqi();
        for (Aqi aqi : allAqi) {
            String[] range = getRange(aqi.getAqiExplain());
            if (value >= Integer.parseInt(range[0]) && value <= Integer.parseInt(range[1])) {
                return new AqiDTO(aqi.getAqiId(), aqi.getChineseExplain(), aqi.getColor());
            }

        }
        return null;
    }

    /**
     * 根据污染物值获取aqi级别
     * @param value 污染物值
     * @param type 污染物类型
     * @return aqi级别
     */
    @Override
    public List<AqiDTO> getAqiLevelByValue(int SO2, int CO, int SPM) {
        int[] aqis = calculateAqi(SO2, CO, SPM);// 为了获取所有aqi信息(缓存s
        AqiDTO so2 = getAqiLevel(aqis[0]);
        AqiDTO co = getAqiLevel(aqis[1]);
        AqiDTO spm = getAqiLevel(aqis[2]);
        AqiDTO aqi = getAqiLevel(aqis[3]);
        return List.of(so2, co, spm, aqi);
    }

    @Override
    public int[] getMinValueByLevel(int level) {
        List<Aqi> allAqi = getAllAqi();
        for (Aqi aqi : allAqi) {
            if (aqi.getAqiId().equals(level)) {
                return new int[]{aqi.getSo2Min(), aqi.getCoMin(), aqi.getSpmMin()};
            }
        }
        return null;
    }


    private record Result(int lowConcentration, int highConcentration) {
    }

    private String[] getRange(String aqiExplain) {
        return aqiExplain.split("-");
    }

    // 计算单一污染物的AQI值
    private int calculateIndividualAqi(int concentration, List<Aqi> allAqi, String type) {
        for (Aqi aqi : allAqi) {
            int lowConcentration = 0, highConcentration = 0;
            String[] range;
            switch (type) {
                case "SO2":
                    lowConcentration = aqi.getSo2Min();
                    highConcentration = aqi.getSo2Max();
                    break;
                case "CO":
                    lowConcentration = aqi.getCoMin();
                    highConcentration = aqi.getCoMax();
                    break;
                case "SPM":
                    lowConcentration = aqi.getSpmMin();
                    highConcentration = aqi.getSpmMax();
                    break;
            }
            range = getRange(aqi.getAqiExplain());
            if (concentration >= lowConcentration && concentration <= highConcentration) {
                return interpolateAqi(concentration, lowConcentration, highConcentration, range);
            }
        }
        return -1; // 无效的浓度值
    }


    /**
     * 插值计算AQI
     *
     * @param concentration     实测污染物浓度
     * @param lowConcentration  浓度区间下限
     * @param highConcentration 浓度区间上限
     * @param aqiRange          AQI指数区间（[低AQI, 高AQI]）
     * @return 计算出的AQI值
     */
    public int interpolateAqi(int concentration, int lowConcentration, int highConcentration, String[] aqiRange) {
        // 提取AQI区间上下限
        int lowAqi = Integer.parseInt(aqiRange[0]);
        int highAqi = Integer.parseInt(aqiRange[1]);

        // 根据公式计算AQI值
        int aqi = (highAqi - lowAqi) * (concentration - lowConcentration) / (highConcentration - lowConcentration) + lowAqi;

        return aqi;
    }
}
