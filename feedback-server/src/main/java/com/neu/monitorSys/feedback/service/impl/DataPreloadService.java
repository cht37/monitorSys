package com.neu.monitorSys.feedback.service.impl;


import com.neu.monitorSys.entity.AqiFeedback;
import com.neu.monitorSys.feedback.service.IAqiFeedbackService;
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
    private static final int PAGE_NUM = 5;
    private static final int PAGE_SIZE = 100;

    /**
     * 数据预加载 5页数据
     */
    @PostConstruct
    public void dataPreload() {
        //判断是否已经加载过数据
        if (aqiFeedbackRepository.findFeedbackByPage("1", 1, 5) != null) {
            return;
        }
        List<AqiFeedback> feedbacks = aqiFeedbackService.findPageBackByPage(5, 100);
        if (feedbacks == null) {
            return;
        }
        aqiFeedbackRepository.saveFeedback(feedbacks);
    }
}
