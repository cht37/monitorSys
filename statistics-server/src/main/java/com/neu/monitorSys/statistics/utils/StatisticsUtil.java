package com.neu.monitorSys.statistics.utils;


import cn.hutool.core.bean.BeanUtil;
import com.neu.monitorSys.common.DTO.AqiDTO;
import com.neu.monitorSys.statistics.VO.StatisticsVO;
import com.neu.monitorSys.statistics.client.AqiClient;
import com.neu.monitorSys.statistics.client.GeoClient;
import com.neu.monitorSys.statistics.client.UserClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Component
public class StatisticsUtil {

    private final GeoClient geoClient;
    private final UserClient userClient;
    private final AqiClient aqiClient;

    public StatisticsUtil(GeoClient geoClient, UserClient userClient, AqiClient aqiClient) {
        this.geoClient = geoClient;
        this.userClient = userClient;
        this.aqiClient = aqiClient;
    }

    public <T> List<StatisticsVO> searchStatisticsData(List<T> statistics) {
        List<CompletableFuture<StatisticsVO>> futures = statistics.stream()
                .map(statistic -> CompletableFuture.supplyAsync(() -> {
                            StatisticsVO statisticsVO = new StatisticsVO();
                            BeanUtil.copyProperties(statistic, statisticsVO);
                            // TODO 查询其他表的数据
                            return statisticsVO;
                        }).thenCombine(
                                // 查询城市名称
                                CompletableFuture.supplyAsync(() -> geoClient.getCityName(getFieldValueString(statistic, "getCityId")).getData()),
                                (statisticsVO, cityName) -> {
                                    statisticsVO.setCityName((String) cityName);
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                // 查询省份名称
                                CompletableFuture.supplyAsync(() -> geoClient.getProvinceName(getFieldValueString(statistic, "getProvinceId")).getData()),
                                (statisticsVO, provinceName) -> {
                                    statisticsVO.setProvinceName((String) provinceName);
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                // 查询区域名称
                                CompletableFuture.supplyAsync(() -> geoClient.getDistrictName(getFieldValueString(statistic, "getDistrictId")).getData()),
                                (statisticsVO, districtName) -> {
                                    statisticsVO.setDistrictName((String) districtName);
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                // 查询网格员姓名
                                CompletableFuture.supplyAsync(() -> userClient.getName(getFieldValueString(statistic, "getGmId")).getData()),
                                (statisticsVO, name) -> {
                                    statisticsVO.setGmName((String) name);
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                // 如果So2VALUE不为空，则查询So2级别
                                CompletableFuture.supplyAsync(() -> {
                                    Number so2Value = getFieldValueNumber(statistic, "getSo2Value");
                                    if (so2Value != null) {
                                        Number so2Aqi = getFieldValueNumber(statistic, "getSo2Aqi");
                                        return aqiClient.getAqiLevel(so2Aqi.intValue()).getData();
                                    }
                                    return null;
                                }), (statisticsVO, aqiDTO) -> {
                                    statisticsVO.setS02Level(BeanUtil.toBean(aqiDTO, AqiDTO.class));
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                // 如果CoAqi不为空，则查询Co级别
                                CompletableFuture.supplyAsync(() -> {
                                    Number coValue = getFieldValueNumber(statistic, "getCoValue");
                                    if (coValue != null) {
                                        Number coAqi = getFieldValueNumber(statistic, "getCoAqi");
                                        return aqiClient.getAqiLevel(coAqi.intValue()).getData();
                                    }
                                    return null;
                                }), (statisticsVO, aqiDTO) -> {
                                    statisticsVO.setCoLevel(BeanUtil.toBean(aqiDTO, AqiDTO.class));
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                // 如果SpmAqi不为空，则查询Spm级别
                                CompletableFuture.supplyAsync(() -> {
                                    Number spmValue = getFieldValueNumber(statistic, "getSpmValue");
                                    if (spmValue != null) {
                                        Number spmAqi = getFieldValueNumber(statistic, "getSpmAqi");
                                        return aqiClient.getAqiLevel(spmAqi.intValue()).getData();
                                    }
                                    return null;
                                }), (statisticsVO, aqiDTO) -> {
                                    statisticsVO.setSpmLevel(BeanUtil.toBean(aqiDTO, AqiDTO.class));
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                // 如果Aqi不为空，则查询Aqi级别
                                CompletableFuture.supplyAsync(() -> {
                                    Number aqi = getFieldValueNumber(statistic, "getAqi");
                                    if (aqi != null) {
                                        return aqiClient.getAqiLevel(aqi.intValue()).getData();
                                    }
                                    return null;
                                }), (statisticsVO, aqiDTO) -> {
                                    statisticsVO.setAqiLevel(BeanUtil.toBean(aqiDTO, AqiDTO.class));
                                    return statisticsVO;
                                }
                        )
                ).toList();
        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }


    private <T> Number getFieldValueNumber(T statistic, String methodName) {
        try {
            return (Number) statistic.getClass().getMethod(methodName).invoke(statistic);
        } catch (Exception e) {
            throw new RuntimeException("Error accessing method: " + methodName, e);
        }
    }


    private <T> String getFieldValueString(T statistic, String methodName) {
        try {
            return (String) statistic.getClass().getMethod(methodName).invoke(statistic);
        } catch (Exception e) {
            throw new RuntimeException("Error accessing method: " + methodName, e);
        }
    }
}
