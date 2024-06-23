package com.neu.monitorSys.statistics.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.entity.AqiFeedback;
import com.neu.monitorSys.entity.GridManager;
import com.neu.monitorSys.entity.Statistics;
import com.neu.monitorSys.statistics.DTO.ReportDTO;
import com.neu.monitorSys.statistics.DTO.StatisticsQueryDTO;
import com.neu.monitorSys.statistics.VO.StatisticsVO;
import com.neu.monitorSys.statistics.client.FeedbackClient;
import com.neu.monitorSys.statistics.client.GeoClient;
import com.neu.monitorSys.statistics.client.UserClient;
import com.neu.monitorSys.statistics.mapper.StatisticsMapper;
import com.neu.monitorSys.statistics.service.IStatisticsService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-17
 */
@Service
public class StatisticsServiceImpl extends ServiceImpl<StatisticsMapper, Statistics> implements IStatisticsService {
    @Autowired
    private StatisticsMapper statisticsMapper;
    @Autowired
    private FeedbackClient feedbackClient;
    @Autowired
    private GeoClient geoClient;
    @Autowired
    private UserClient userClient;

    /**
     * 统计分析数据（可能是异步任务）
     */
    @Override
    public void statisticsData(ReportDTO reportDTO) {

    }

    @Override
    @GlobalTransactional
    public void gridManagerReport(ReportDTO reportDTO, String logId) {
        //1.通过afUId获取feedback记录中的地址以及详情
        Object data = feedbackClient.findFeedbackById(reportDTO.getAfId()).getData();
        AqiFeedback feedback = BeanUtil.toBean(data, AqiFeedback.class);
        //2.判断feedback状态，如果是已处理，则抛出异常
        if (feedback.getState() == 2) {
            throw new RuntimeException("该反馈已处理");
        } else if (feedback.getState() == 0) {
            throw new RuntimeException("该反馈未指派");
        }
        //4.复制属性到statistics
        Statistics statistics = new Statistics();
        statistics.setProvinceId(feedback.getProvinceId());
        statistics.setCityId(feedback.getCityId());
        statistics.setDistrictId(feedback.getDistrictId());
        statistics.setAddress(feedback.getAddress());
        statistics.setFdTel(feedback.getTelId());
        statistics.setInformation(feedback.getInformation());
        BeanUtil.copyProperties(reportDTO, statistics);
        //判断是否存在afId 相同的记录
        LambdaQueryWrapper<Statistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Statistics::getAfId, reportDTO.getAfId());
        Statistics one = statisticsMapper.selectOne(wrapper);
        if (one != null) {
            throw new RuntimeException("该反馈已上报");
        }
        //5.写入入statistics
        statisticsMapper.insert(statistics);
        //6.修改feedback状态，为已确认
        feedbackClient.updateFeedbackState(reportDTO.getAfId(), 2);
        //7.修改网格员状态
        GridManager gridManager = new GridManager();
        gridManager.setAfId(null);
        gridManager.setMemberId(logId);
        gridManager.setAreaId(null);
        //设置状态为可工作状态
        gridManager.setState(0);
        //TODO 8.发布异步消息，计算aqi
    }

    @Override
    public IPage<StatisticsVO> queryStatisticsData(StatisticsQueryDTO statisticsQueryDTO, int page, int size) {
        //如果id不为空，则直接查询
        if (statisticsQueryDTO.getId() != null || statisticsQueryDTO.getAfId() != null) {
            LambdaQueryWrapper<Statistics> wrapper = new LambdaQueryWrapper<>();
            if (statisticsQueryDTO.getId() != null || statisticsQueryDTO.getAfId() != null) {
                wrapper.eq(Statistics::getId, statisticsQueryDTO.getId());
            }
            if (statisticsQueryDTO.getAfId() != null) {
                wrapper.eq(Statistics::getAfId, statisticsQueryDTO.getAfId());
            }

            return recollect(page, size, wrapper);
        }
        //如果id为空，则根据条件查询
        LambdaQueryWrapper<Statistics> wrapper = new LambdaQueryWrapper<>();
        //如果省市区名称不为空
        if (statisticsQueryDTO.getProvinceName() != null || statisticsQueryDTO.getCityName() != null || statisticsQueryDTO.getDistrictName() != null) {
            //获取省份编号
            String provinceId = (String) geoClient.getProvinceId(statisticsQueryDTO.getProvinceName()).getData();
            if (provinceId == null || provinceId.equals("")) {
                return null;
            }
            //获取城市编号
            String cityId = (String) geoClient.getCityIdByProvinceId(statisticsQueryDTO.getCityName(), provinceId).getData();
            if (cityId == null || cityId.equals("")) {
                return null;
            }
            //获取区域编号
            String districtId = (String) geoClient.getDistrictId(statisticsQueryDTO.getDistrictName(), cityId).getData();
            if (districtId == null || districtId.equals("")) {
                return null;
            }
            wrapper.eq(Statistics::getCityId, cityId);
            wrapper.eq(Statistics::getProvinceId, provinceId);
            wrapper.eq(Statistics::getDistrictId, districtId);
        }
        //如果so2上下限不为空
        if (statisticsQueryDTO.getSo2ValueMax() != null || statisticsQueryDTO.getSo2ValueMin() != null) {
            //如果上限不为空，下限为空
            if (statisticsQueryDTO.getSo2ValueMax() != null && statisticsQueryDTO.getSo2ValueMin() == null) {
                wrapper.le(Statistics::getSo2Value, statisticsQueryDTO.getSo2ValueMax());
            }
            //如果下限不为空，上限为空
            if (statisticsQueryDTO.getSo2ValueMin() != null && statisticsQueryDTO.getSo2ValueMax() == null) {
                wrapper.ge(Statistics::getSo2Value, statisticsQueryDTO.getSo2ValueMin());
            }
            //如果上下限都不为空
            if (statisticsQueryDTO.getSo2ValueMin() != null && statisticsQueryDTO.getSo2ValueMax() != null) {
                wrapper.between(Statistics::getSo2Value, statisticsQueryDTO.getSo2ValueMin(), statisticsQueryDTO.getSo2ValueMax());
            }
        }
        //如果co上下限不为空
        if (statisticsQueryDTO.getCoValueMax() != null || statisticsQueryDTO.getCoValueMin() != null) {
            //如果上限不为空，下限为空
            if (statisticsQueryDTO.getCoValueMax() != null && statisticsQueryDTO.getCoValueMin() == null) {
                wrapper.le(Statistics::getCoValue, statisticsQueryDTO.getCoValueMax());
            }
            //如果下限不为空，上限为空
            if (statisticsQueryDTO.getCoValueMin() != null && statisticsQueryDTO.getCoValueMax() == null) {
                wrapper.ge(Statistics::getCoValue, statisticsQueryDTO.getCoValueMin());
            }
            //如果上下限都不为空
            if (statisticsQueryDTO.getCoValueMin() != null && statisticsQueryDTO.getCoValueMax() != null) {
                wrapper.between(Statistics::getCoValue, statisticsQueryDTO.getCoValueMin(), statisticsQueryDTO.getCoValueMax());
            }
        }
        //如果spm上下限不为空
        if (statisticsQueryDTO.getSpmValueMax() != null || statisticsQueryDTO.getSpmValueMin() != null) {
            //如果上限不为空，下限为空
            if (statisticsQueryDTO.getSpmValueMax() != null && statisticsQueryDTO.getSpmValueMin() == null) {
                wrapper.le(Statistics::getSpmValue, statisticsQueryDTO.getSpmValueMax());
            }
            //如果下限不为空，上限为空
            if (statisticsQueryDTO.getSpmValueMin() != null && statisticsQueryDTO.getSpmValueMax() == null) {
                wrapper.ge(Statistics::getSpmValue, statisticsQueryDTO.getSpmValueMin());
            }
            //如果上下限都不为空
            if (statisticsQueryDTO.getSpmValueMin() != null && statisticsQueryDTO.getSpmValueMax() != null) {
                wrapper.between(Statistics::getSpmValue, statisticsQueryDTO.getSpmValueMin(), statisticsQueryDTO.getSpmValueMax());
            }
        }
        //如果aqi上下限不为空
        if (statisticsQueryDTO.getAqiMax() != null || statisticsQueryDTO.getAqiMin() != null) {
            //如果上限不为空，下限为空
            if (statisticsQueryDTO.getAqiMax() != null && statisticsQueryDTO.getAqiMin() == null) {
                wrapper.le(Statistics::getAqi, statisticsQueryDTO.getAqiMax());
            }
            //如果下限不为空，上限为空
            if (statisticsQueryDTO.getAqiMin() != null && statisticsQueryDTO.getAqiMax() == null) {
                wrapper.ge(Statistics::getAqi, statisticsQueryDTO.getAqiMin());
            }
            //如果上下限都不为空
            if (statisticsQueryDTO.getAqiMin() != null && statisticsQueryDTO.getAqiMax() != null) {
                wrapper.between(Statistics::getAqi, statisticsQueryDTO.getAqiMin(), statisticsQueryDTO.getAqiMax());
            }
        }
        //如果网格员编号不为空
        if (statisticsQueryDTO.getGmId() != null) {
            wrapper.eq(Statistics::getGmId, statisticsQueryDTO.getGmId());
        }
        //如果公众监督员电话不为空
        if (statisticsQueryDTO.getFdTel() != null) {
            wrapper.eq(Statistics::getFdTel, statisticsQueryDTO.getFdTel());
        }
        //四种排序方式 s02Ascending coAscending spmAscending aqiAscending，只能选择一种，如果选择多种，则按照最后一种排序
        if (statisticsQueryDTO.getSo2Ascending() != null) {
            if (statisticsQueryDTO.getSo2Ascending()) {
                wrapper.orderByAsc(Statistics::getSo2Value);
            } else {
                wrapper.orderByDesc(Statistics::getSo2Value);
            }
        }
        if (statisticsQueryDTO.getCoAscending() != null) {
            if (statisticsQueryDTO.getCoAscending()) {
                wrapper.orderByAsc(Statistics::getCoValue);
            } else {
                wrapper.orderByDesc(Statistics::getCoValue);
            }
        }
        if (statisticsQueryDTO.getSpmAscending() != null) {
            if (statisticsQueryDTO.getSpmAscending()) {
                wrapper.orderByAsc(Statistics::getSpmValue);
            } else {
                wrapper.orderByDesc(Statistics::getSpmValue);
            }
        }
        if (statisticsQueryDTO.getAqiAscending() != null) {
            if (statisticsQueryDTO.getAqiAscending()) {
                wrapper.orderByAsc(Statistics::getAqi);
            } else {
                wrapper.orderByDesc(Statistics::getAqi);
            }
        }
        return recollect(page, size, wrapper);
    }

    /**
     * 重新收集数据
     * @param page 页数
     * @param size 每页大小
     * @param wrapper 查询条件
     * @return 查询结果
     */
    private IPage<StatisticsVO> recollect(int page, int size, LambdaQueryWrapper<Statistics> wrapper) {
        IPage<Statistics> statisticsIPage = statisticsMapper.selectPage(new Page<>(page, size), wrapper);
        List<StatisticsVO> statisticsData = searchStatisticsData(statisticsIPage.getRecords());
        IPage<StatisticsVO> statisticsVOIPage = new Page<>();
        statisticsVOIPage.setRecords(statisticsData);
        statisticsVOIPage.setTotal(statisticsIPage.getTotal());
        statisticsIPage.setPages(statisticsIPage.getPages());
        statisticsVOIPage.setCurrent(page);
        statisticsVOIPage.setSize(size);
        return statisticsVOIPage;
    }

    /**
     * 异步查询需要拼接的数据
     *
     * @param statistics 查询到的数据集合
     * @return 拼接后的数据
     */
    private List<StatisticsVO> searchStatisticsData(List<Statistics> statistics) {
        List<CompletableFuture<StatisticsVO>> futures = statistics.stream()
                .map(statistic -> CompletableFuture.supplyAsync(() -> {
                            StatisticsVO statisticsVO = new StatisticsVO();
                            BeanUtil.copyProperties(statistic, statisticsVO);
                            //TODO 查询其他表的数据
                            return statisticsVO;
                        }).thenCombine(
                                //查询城市名称
                                CompletableFuture.supplyAsync(() -> geoClient.getCityName(statistic.getCityId()).getData()),
                                (statisticsVO, cityName) -> {
                                    statisticsVO.setCityName((String) cityName);
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                //查询省份名称
                                CompletableFuture.supplyAsync(() -> geoClient.getProvinceName(statistic.getProvinceId()).getData()),
                                (statisticsVO, provinceName) -> {
                                    statisticsVO.setProvinceName((String) provinceName);
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                //查询区域名称
                                CompletableFuture.supplyAsync(() -> geoClient.getDistrictName(statistic.getDistrictId()).getData()),
                                (statisticsVO, districtName) -> {
                                    statisticsVO.setDistrictName((String) districtName);
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                //查询网格员姓名
                                CompletableFuture.supplyAsync(() -> userClient.getName(statistic.getGmId()).getData()),
                                (statisticsVO, name) -> {
                                    statisticsVO.setGmName((String) name);
                                    return statisticsVO;
                                }
                        )
                        //todo 结合aqi级别数据
                ).toList();
        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

    }


}
