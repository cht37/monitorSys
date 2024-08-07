package com.neu.monitor_sys.feedback.service.impl;


import com.neu.monitor_sys.common.entity.AqiFeedback;
import com.neu.monitor_sys.feedback.constants.FeedbackRedisPrefix;
import com.neu.monitor_sys.feedback.service.IAqiFeedbackService;
import com.neu.monitor_sys.feedback.util.RedisUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据预加载服务
 */
@Service
public class DataPreloadService {
    @Autowired
    private AqiFeedbackRepository aqiFeedbackRepository;
    @Autowired
    private IAqiFeedbackService aqiFeedbackService;
    @Autowired
    private RedisUtil redisUtil;
    private static final int PAGE_NUM = 5;
    private static final int PAGE_SIZE = 100;

    /**
     * 数据预加载 5页数据
     */
    @PostConstruct
    public void dataPreload() {
        //删除旧数据
        redisUtil.del(FeedbackRedisPrefix.FEEDBACK_LIST);
        redisUtil.del(FeedbackRedisPrefix.FEEDBACK_DATA);
        //获取5页数据
        List<AqiFeedback> feedbacks = aqiFeedbackService.findPageBackByPage(5, 100);
        if (feedbacks == null) {
            return;
        }
        //存入redis
        aqiFeedbackRepository.saveFeedback(feedbacks);
    }
}
