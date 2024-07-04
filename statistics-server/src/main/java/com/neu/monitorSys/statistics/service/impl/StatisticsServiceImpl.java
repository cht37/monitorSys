package com.neu.monitorSys.statistics.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.common.DTO.AqiDTO;
import com.neu.monitorSys.common.DTO.MyResponse;
import com.neu.monitorSys.common.constants.ResultCode;
import com.neu.monitorSys.common.entity.AqiFeedback;
import com.neu.monitorSys.common.entity.GridManager;
import com.neu.monitorSys.common.entity.Statistics;
import com.neu.monitorSys.common.entity.StatisticsES;
import com.neu.monitorSys.statistics.DTO.PollutionStatisticsDTO;
import com.neu.monitorSys.statistics.DTO.ProvinceAqiStatsDTO;
import com.neu.monitorSys.statistics.DTO.ReportDTO;
import com.neu.monitorSys.statistics.DTO.StatisticsQueryDTO;
import com.neu.monitorSys.statistics.VO.StatisticsVO;
import com.neu.monitorSys.statistics.client.AqiClient;
import com.neu.monitorSys.statistics.client.FeedbackClient;
import com.neu.monitorSys.statistics.client.GeoClient;
import com.neu.monitorSys.statistics.client.UserClient;
import com.neu.monitorSys.statistics.entity.AqiStatisticsPercent;
import com.neu.monitorSys.statistics.mapper.StatisticsMapper;
import com.neu.monitorSys.statistics.publisher.StatisticsPublisher;
import com.neu.monitorSys.statistics.repository.EsStatisticsRepository;
import com.neu.monitorSys.statistics.service.IStatisticsService;
import com.neu.monitorSys.statistics.utils.StatisticsUtil;
import io.seata.spring.annotation.GlobalTransactional;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.ValueCount;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    @Autowired
    private AqiClient aqiClient;

    @Autowired
    private StatisticsPublisher statisticsPublisher;

    @Autowired
    private EsStatisticsRepository repository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private StatisticsUtil statisticsUtil;


    @Autowired
    private RestHighLevelClient client;

    /**
     * 统计分析数据（可能是异步任务）
     */
    @Override
    public void statisticsData(ReportDTO reportDTO) {

    }

    /**
     * 网格员上报数据
     *
     * @param reportDTO 上报数据
     * @param logId     网格员id
     */
    @Override
    @GlobalTransactional
    public void gridManagerReport(ReportDTO reportDTO, String logId) {
        //通过logId获取afId
        MyResponse<Integer> response = userClient.getAfIdByLogId(logId);
        Integer afId = response.getData();
        if (afId == null) {
            throw new RuntimeException("网格员未指派");
        }
        reportDTO.setAfId(afId);
        //1.通过afUId获取feedback记录中的地址以及详情
        Object data = feedbackClient.findFeedbackById(reportDTO.getAfId()).getData();
        AqiFeedback feedback = BeanUtil.toBean(data, AqiFeedback.class);
        //2.判断feedback状态，如果是已处理，则抛出异常
        if (feedback.getState() == 3) {
            throw new RuntimeException("该反馈已处理");
        } else if (feedback.getState() == 0) {
            throw new RuntimeException("该反馈未指派");
        }
        //3.属性不为空判断
        if (reportDTO.getSo2Value() == null || reportDTO.getCoValue() == null || reportDTO.getSpmValue() == null) {
            throw new RuntimeException("数据不完整");
        }
        //4.复制属性到statistics
        Statistics statistics = new Statistics();
        statistics.setProvinceId(feedback.getProvinceId());
        statistics.setCityId(feedback.getCityId());
        statistics.setDistrictId(feedback.getDistrictId());
        statistics.setAddress(feedback.getAddress());
        statistics.setFdTel(feedback.getTelId());
        statistics.setGmId(logId);
        statistics.setInformation(feedback.getInformation());
        //设置确认时间
        statistics.setConfirmDatetime(DateTime.now().toLocalDateTime());
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
        feedbackClient.updateFeedbackState(reportDTO.getAfId(), 3);
        //7.修改网格员状态
        GridManager gridManager = new GridManager();
        gridManager.setAfId(null);
        gridManager.setMemberId(logId);
        gridManager.setAreaId(null);
        //设置状态为可工作状态
        gridManager.setState(0);
        int code = userClient.editGridMember(gridManager).getStatusCode();
        if (code != ResultCode.SUCCESS.getCode()) {
            throw new RuntimeException("网格员状态修改失败");
        }
        boolean send;
        try {
            send = statisticsPublisher.sendStaticsData(reportDTO);
        } catch (Exception e) {
            throw new RuntimeException("消息发送失败");
        }
        if (!send) {
            throw new RuntimeException("消息发送失败");
        }
    }

    /**
     * 分条件查询统计数据
     *
     * @param statisticsQueryDTO 查询条件
     * @param page               页数
     * @param size               每页大小
     * @return 查询结果
     */
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
            String provinceId = null;
            if (statisticsQueryDTO.getProvinceName() != null) {
                //获取省份编号
                provinceId = (String) geoClient.getProvinceId(statisticsQueryDTO.getProvinceName()).getData();
                if (provinceId == null || provinceId.equals("")) {
                    return new Page<>();
                }
                wrapper.eq(Statistics::getProvinceId, provinceId);
            }
            String cityId = null;
            if (statisticsQueryDTO.getCityName() != null && provinceId != null) {
                //获取城市编号
                cityId = (String) geoClient.getCityIdByProvinceId(statisticsQueryDTO.getCityName(), provinceId).getData();
                if (cityId == null || cityId.equals("")) {
                    return new Page<>();
                }
                wrapper.eq(Statistics::getCityId, cityId);
            }
            String districtId = null;
            if (statisticsQueryDTO.getDistrictName() != null && cityId != null) {
                //获取区域编号
                districtId = (String) geoClient.getDistrictId(statisticsQueryDTO.getDistrictName(), cityId).getData();
                if (districtId != null && !districtId.equals("")) {
                    return new Page<>();
                }
                wrapper.eq(Statistics::getDistrictId, districtId);
            }
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
        //重新收集数据
        return recollect(page, size, wrapper);
    }

    @Override
    public IPage<StatisticsVO> queryStatisticsDataById(String logId, int page, int size) {
        LambdaQueryWrapper<Statistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Statistics::getGmId, logId);
        wrapper.orderByAsc(Statistics::getConfirmDatetime);
        return recollect(page, size, wrapper);
    }


    /**
     * 重新收集数据
     *
     * @param page    页数
     * @param size    每页大小
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
                        ).thenCombine(
                                //如果So2Aqi不为空，则查询So2级别
                                CompletableFuture.supplyAsync(() -> {
                                    if (statistic.getSo2Aqi() != null) {
                                        return aqiClient.getAqiLevel(statistic.getSo2Aqi()).getData();
                                    }
                                    return null;
                                }), (statisticsVO, aqiDTO) -> {
                                    statisticsVO.setS02Level(BeanUtil.toBean(aqiDTO, AqiDTO.class));
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                //如果CoAqi不为空，则查询Co级别
                                CompletableFuture.supplyAsync(() -> {
                                    if (statistic.getCoValue() != null) {
                                        return aqiClient.getAqiLevel(statistic.getAqi()).getData();
                                    }
                                    return null;
                                }), (statisticsVO, aqiDTO) -> {
                                    statisticsVO.setCoLevel(BeanUtil.toBean(aqiDTO, AqiDTO.class));
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                //如果SpmAqi不为空，则查询Spm级别
                                CompletableFuture.supplyAsync(() -> {
                                    if (statistic.getSpmValue() != null) {
                                        return aqiClient.getAqiLevel(statistic.getSo2Aqi()).getData();
                                    }
                                    return null;
                                }), (statisticsVO, aqiDTO) -> {
                                    statisticsVO.setSpmLevel(BeanUtil.toBean(aqiDTO, AqiDTO.class));
                                    return statisticsVO;
                                }
                        ).thenCombine(
                                //如果Aqi不为空，则查询Aqi级别
                                CompletableFuture.supplyAsync(() -> {
                                    if (statistic.getAqi() != null) {
                                        return aqiClient.getAqiLevel(statistic.getAqi()).getData();
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


    @Override
    public IPage<StatisticsVO> queryStatisticsDataES(StatisticsQueryDTO statisticsQueryDTO, int page, int size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 添加查询条件
        if (statisticsQueryDTO.getId() != null) {
            boolQuery.must(QueryBuilders.termQuery("id", statisticsQueryDTO.getId()));
        }
        if (statisticsQueryDTO.getAfId() != null) {
            boolQuery.must(QueryBuilders.termQuery("af_id", statisticsQueryDTO.getAfId()));
        }
        //如果省市区名称有一个不为空，则添加查询条件，需要首先查询省份编号
        String provinceId = null, cityId = null, districtId = null;
        if (statisticsQueryDTO.getProvinceName() != null || statisticsQueryDTO.getCityName() != null || statisticsQueryDTO.getDistrictName() != null) {
            if (statisticsQueryDTO.getProvinceName() == null) {
                throw new RuntimeException("省份名称不能为空");
            }
            provinceId = (String) geoClient.getProvinceId(statisticsQueryDTO.getProvinceName()).getData();
            if (provinceId == null || provinceId.equals("")) {
                throw new RuntimeException("省份名称错误");
            }
            if (statisticsQueryDTO.getCityName() != null) {
                cityId = (String) geoClient.getCityIdByProvinceId(statisticsQueryDTO.getCityName(), provinceId).getData();
                if (cityId == null || cityId.equals("")) {
                    throw new RuntimeException("城市名称错误");
                }
            }
            if (statisticsQueryDTO.getDistrictName() != null) {
                districtId = (String) geoClient.getDistrictId(statisticsQueryDTO.getDistrictName(), cityId).getData();
                if (districtId == null || districtId.equals("")) {
                    throw new RuntimeException("区域名称错误");
                }
            }

        }

        if (provinceId != null) {
            boolQuery.must(QueryBuilders.termQuery("province_id.keyword", provinceId));
        }
        if (cityId != null) {
            boolQuery.must(QueryBuilders.termQuery("city_id.keyword", cityId));
        }
        if (districtId != null) {
            boolQuery.must(QueryBuilders.termQuery("district_id.keyword", districtId));
        }
        if (statisticsQueryDTO.getAddress() != null) {
            boolQuery.must(QueryBuilders.termQuery("address.keyword", statisticsQueryDTO.getAddress()));
        }
        if (statisticsQueryDTO.getSo2ValueMax() != null) {
            boolQuery.must(QueryBuilders.rangeQuery("so2_value").lte(statisticsQueryDTO.getSo2ValueMax()));
        }
        if (statisticsQueryDTO.getSo2ValueMin() != null) {
            boolQuery.must(QueryBuilders.rangeQuery("so2_value").gte(statisticsQueryDTO.getSo2ValueMin()));
        }
        if (statisticsQueryDTO.getCoValueMax() != null) {
            boolQuery.must(QueryBuilders.rangeQuery("co_value").lte(statisticsQueryDTO.getCoValueMax()));
        }
        if (statisticsQueryDTO.getCoValueMin() != null) {
            boolQuery.must(QueryBuilders.rangeQuery("co_value").gte(statisticsQueryDTO.getCoValueMin()));
        }
        if (statisticsQueryDTO.getSpmValueMax() != null) {
            boolQuery.must(QueryBuilders.rangeQuery("spm_value").lte(statisticsQueryDTO.getSpmValueMax()));
        }
        if (statisticsQueryDTO.getSpmValueMin() != null) {
            boolQuery.must(QueryBuilders.rangeQuery("spm_value").gte(statisticsQueryDTO.getSpmValueMin()));
        }
        if (statisticsQueryDTO.getAqiMax() != null) {
            boolQuery.must(QueryBuilders.rangeQuery("aqi").lte(statisticsQueryDTO.getAqiMax()));
        }
        if (statisticsQueryDTO.getAqiMin() != null) {
            boolQuery.must(QueryBuilders.rangeQuery("aqi").gte(statisticsQueryDTO.getAqiMin()));
        }
        if (statisticsQueryDTO.getConfirmDatetime() != null) {
            boolQuery.must(QueryBuilders.termQuery("confirm_datetime", statisticsQueryDTO.getConfirmDatetime()));
        }
        if (statisticsQueryDTO.getGmId() != null) {
            boolQuery.must(QueryBuilders.termQuery("gm_id.keyword", statisticsQueryDTO.getGmId()));
        }
        if (statisticsQueryDTO.getFdTel() != null) {
            boolQuery.must(QueryBuilders.termQuery("fd_tel.keyword", statisticsQueryDTO.getFdTel()));
        }

        // 构建查询
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(page, size));

        // 添加排序条件
        if (statisticsQueryDTO.getSo2Ascending() != null) {
            searchQueryBuilder.withSorts(SortBuilders.fieldSort("so2_value")
                    .order(statisticsQueryDTO.getSo2Ascending() ? SortOrder.ASC : SortOrder.DESC));
        }
        if (statisticsQueryDTO.getCoAscending() != null) {
            searchQueryBuilder.withSorts(SortBuilders.fieldSort("co_value")
                    .order(statisticsQueryDTO.getCoAscending() ? SortOrder.ASC : SortOrder.DESC));
        }
        if (statisticsQueryDTO.getSpmAscending() != null) {
            searchQueryBuilder.withSorts(SortBuilders.fieldSort("spm_value")
                    .order(statisticsQueryDTO.getSpmAscending() ? SortOrder.ASC : SortOrder.DESC));
        }
        if (statisticsQueryDTO.getAqiAscending() != null) {
            searchQueryBuilder.withSorts(SortBuilders.fieldSort("aqi")
                    .order(statisticsQueryDTO.getAqiAscending() ? SortOrder.ASC : SortOrder.DESC));
        }
        SearchPage<StatisticsES> searchPage = null;
        // 执行查询
        if (boolQuery.hasClauses()) {
            NativeSearchQuery searchQuery = searchQueryBuilder.build();
            SearchHits<StatisticsES> searchHits = elasticsearchRestTemplate.search(searchQuery, StatisticsES.class);
            searchPage = SearchHitSupport.searchPageFor(searchHits, PageRequest.of(page, size));
        } else {
            searchPage = queryAllStatisticsData(page, size);
        }
        //转换为List<StatisticsES>
        List<StatisticsES> statistics = searchPage.getSearchHits().stream().map(SearchHit::getContent).toList();
        //转换为List<StatisticsVO>
//        List<StatisticsVO> statisticsVOS = searchStatisticsData(statistics);
        List<StatisticsVO> statisticsVOS = statisticsUtil.searchStatisticsData(statistics);
        //返回查询结果
        IPage<StatisticsVO> statisticsVOIPage = new Page<>();
        statisticsVOIPage.setRecords(statisticsVOS);
        statisticsVOIPage.setTotal(searchPage.getTotalElements());
        statisticsVOIPage.setPages(searchPage.getTotalPages());
        statisticsVOIPage.setCurrent(page);
        statisticsVOIPage.setSize(size);
        return statisticsVOIPage;
    }

    /**
     * 查询所有统计数据es
     *
     * @param page 页数
     * @param size 每页大小
     * @return 统计数据
     */
    public SearchPage<StatisticsES> queryAllStatisticsData(int page, int size) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<StatisticsES> searchHits = elasticsearchRestTemplate.search(searchQuery, StatisticsES.class);
        return SearchHitSupport.searchPageFor(searchHits, PageRequest.of(page, size));
    }

    public List<SearchHit<StatisticsES>> queryAllStatisticsData() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();

        SearchHits<StatisticsES> searchHits = elasticsearchRestTemplate.search(searchQuery, StatisticsES.class);
        searchHits.forEach(searchHit -> {
            System.out.println(searchHit.getContent());
        });
        return searchHits.getSearchHits();
    }

    /**
     * 获取省级空气质量统计信息
     *
     * @return List 省级空气质量统计信息
     * @throws IOException IO异常
     */
    @Override
    public List<ProvinceAqiStatsDTO> getProvinceAqiStatistics() throws IOException {
        SearchRequest searchRequest = new SearchRequest("statistics_es");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //时间范围，最近一周
        searchSourceBuilder.query(QueryBuilders.rangeQuery("confirm_datetime")
                .gte("now-7d/d")
                .lte("now/d"));
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.aggregation(AggregationBuilders.terms("by_province")
                .field("province_id.keyword")
                .subAggregation(AggregationBuilders.avg("avg_so2_value").field("so2_value"))
                .subAggregation(AggregationBuilders.avg("avg_co_value").field("co_value"))
                .subAggregation(AggregationBuilders.avg("avg_spm_value").field("spm_value"))
                .subAggregation(AggregationBuilders.avg("avg_aqi").field("aqi"))
        );
        searchSourceBuilder.size(0); // We don't need the actual documents, just the aggregation results
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        Aggregations aggregations = searchResponse.getAggregations();
        Terms byProvince = aggregations.get("by_province");

        List<ProvinceAqiStatsDTO> stats = new ArrayList<>();
        for (Terms.Bucket bucket : byProvince.getBuckets()) {
            ProvinceAqiStatsDTO dto = new ProvinceAqiStatsDTO();
            dto.setProvinceId(bucket.getKeyAsString());
            Avg avgSo2Value = bucket.getAggregations().get("avg_so2_value");
            Avg avgCoValue = bucket.getAggregations().get("avg_co_value");
            Avg avgSpmValue = bucket.getAggregations().get("avg_spm_value");
            Avg avgAqi = bucket.getAggregations().get("avg_aqi");
            dto.setAvgSo2Value(avgSo2Value.getValue());
            dto.setAvgCoValue(avgCoValue.getValue());
            dto.setAvgSpmValue(avgSpmValue.getValue());
            dto.setAvgAqi(avgAqi.getValue());
            stats.add(dto);
        }
        for (ProvinceAqiStatsDTO stat : stats) {
            String provinceName = (String) geoClient.getProvinceName(stat.getProvinceId()).getData();
            stat.setProvinceName(provinceName);
        }
        return stats;
    }

    @Override
    public List<AqiStatisticsPercent> getAqiLevelPercent() throws IOException {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();
        SearchHits<AqiStatisticsPercent> searchHits = elasticsearchRestTemplate.search(searchQuery, AqiStatisticsPercent.class);
        return searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());

    }

    /**
     * 获取省级污染统计信息
     *
     * @param level 污染等级
     * @return 省级污染统计信息（每个周大于污染物限额的次数）
     * @throws IOException
     */
    @Override
    public List<PollutionStatisticsDTO> getProvincePollutionStats(Integer level) throws IOException {
        // 获取污染限值
        MyResponse<int[]> response = aqiClient.getMinValueByLevel(level);
        if (response.getStatusCode() != ResultCode.SUCCESS.getCode()) {
            throw new RuntimeException("获取污染限值失败");
        }
        int[] minValues = response.getData();
        int so2Min = minValues[0];
        int coMin = minValues[1];
        int spmMin = minValues[2];

        // 构建查询
        SearchRequest searchRequest = new SearchRequest("statistics_es");

        // 省份聚合
        TermsAggregationBuilder provinceAgg = AggregationBuilders.terms("by_province")
                .field("province_id.keyword");

        // 按周统计
        DateHistogramAggregationBuilder weekAgg = AggregationBuilders.dateHistogram("by_week")
                .field("confirm_datetime")
                .calendarInterval(DateHistogramInterval.WEEK);

        // 计算SO2超标次数
        FilterAggregationBuilder so2ExceedFilter = AggregationBuilders.filter("so2_exceed_filter",
                QueryBuilders.rangeQuery("so2_value").gte(so2Min));
        ValueCountAggregationBuilder so2ExceedCount = AggregationBuilders.count("so2_exceed_count")
                .field("so2_value");
        so2ExceedFilter.subAggregation(so2ExceedCount);

        // 计算CO超标次数
        FilterAggregationBuilder coExceedFilter = AggregationBuilders.filter("co_exceed_filter",
                QueryBuilders.rangeQuery("co_value").gte(coMin));
        ValueCountAggregationBuilder coExceedCount = AggregationBuilders.count("co_exceed_count")
                .field("co_value");
        coExceedFilter.subAggregation(coExceedCount);

        // 计算SPM超标次数
        FilterAggregationBuilder spmExceedFilter = AggregationBuilders.filter("spm_exceed_filter",
                QueryBuilders.rangeQuery("spm_value").gte(spmMin));
        ValueCountAggregationBuilder spmExceedCount = AggregationBuilders.count("spm_exceed_count")
                .field("spm_value");
        spmExceedFilter.subAggregation(spmExceedCount);

        // 添加聚合
        weekAgg.subAggregation(so2ExceedFilter);
        weekAgg.subAggregation(coExceedFilter);
        weekAgg.subAggregation(spmExceedFilter);

        provinceAgg.subAggregation(weekAgg);

        // 构建请求对象
        searchRequest.source().query(QueryBuilders.matchAllQuery())
                .aggregation(provinceAgg);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        Terms provinceTerms = searchResponse.getAggregations().get("by_province");
        List<PollutionStatisticsDTO> stats = new ArrayList<>();

        for (Terms.Bucket provinceBucket : provinceTerms.getBuckets()) {
            String provinceName = provinceBucket.getKeyAsString();
            Histogram weeks = provinceBucket.getAggregations().get("by_week");

            for (Histogram.Bucket weekBucket : weeks.getBuckets()) {
                PollutionStatisticsDTO dto = new PollutionStatisticsDTO();
                dto.setProvinceName(provinceName);
                String string = weekBucket.getKeyAsString();
                LocalDateTime time = LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
                dto.setTime(time);

                Filter so2Exceed = weekBucket.getAggregations().get("so2_exceed_filter");
                long so2ExceedCountValue = ((ValueCount) so2Exceed.getAggregations().get("so2_exceed_count")).getValue();
                dto.setSo2ExceedTimes((int) so2ExceedCountValue);

                Filter coExceed = weekBucket.getAggregations().get("co_exceed_filter");
                long coExceedCountValue = ((ValueCount) coExceed.getAggregations().get("co_exceed_count")).getValue();
                dto.setCoExceedTimes((int) coExceedCountValue);

                Filter spmExceed = weekBucket.getAggregations().get("spm_exceed_filter");
                long spmExceedCountValue = ((ValueCount) spmExceed.getAggregations().get("spm_exceed_count")).getValue();
                dto.setSpmExceedTimes((int) spmExceedCountValue);

                stats.add(dto);
            }
        }

        return stats;
    }


}
