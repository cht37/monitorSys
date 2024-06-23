package com.neu.monitorSys.statistics.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitorSys.entity.AqiFeedback;
import com.neu.monitorSys.entity.GridManager;
import com.neu.monitorSys.entity.Statistics;
import com.neu.monitorSys.statistics.DTO.ReportDTO;
import com.neu.monitorSys.statistics.VO.StatisticsVO;
import com.neu.monitorSys.statistics.client.FeedbackClient;
import com.neu.monitorSys.statistics.mapper.StatisticsMapper;
import com.neu.monitorSys.statistics.service.IStatisticsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
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
        if (feedback.getState()==2) {
            throw new RuntimeException("该反馈已处理");
        } else if (feedback.getState()==0) {
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
        GridManager gridManager=new GridManager();
        gridManager.setAfId(null);
        gridManager.setMemberId(logId);
        gridManager.setAreaId(null);
        //设置状态为可工作状态
        gridManager.setState(0);
        //TODO 8.发布异步消息，计算aqi
    }

    @Override
    public IPage<StatisticsVO> queryStatisticsData() {

        return null;
    }


}
