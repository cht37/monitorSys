package com.neu.monitorSys.feedback.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitorSys.entity.AqiFeedback;
import com.neu.monitorSys.feedback.DTO.AqiFeedbackDTO;
import com.neu.monitorSys.feedback.constants.FeedbackRedisPrefix;
import com.neu.monitorSys.feedback.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AqiFeedbackRepository {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void saveFeedback(AqiFeedback feedback) {
        // 保存到Sorted Set，使用时间戳作为分数
        redisUtil.zAdd(FeedbackRedisPrefix.FEEDBACK_LIST + feedback.getTelId(), feedback.getAfId().toString(), feedback.getTimestamp());
        // 保存详细数据到Hash
        redisUtil.hset(FeedbackRedisPrefix.FEEDBACK_DATA, String.valueOf(feedback.getAfId()), feedback);
    }
    public void updateFeedbackData(AqiFeedback feedback){
        redisUtil.hset(FeedbackRedisPrefix.FEEDBACK_DATA, String.valueOf(feedback.getAfId()), feedback);
    }
    public void saveFeedback(List<AqiFeedback> feedbacks) {
        // 设置序列化器
        RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
        // 使用管道方式插入数据 减少与redis的频繁io 提升性能
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            // 将每个VO对象添加到sorted set中，使用下标作为分数(如果有其他业务 可以用其他代替下标 比如评分、积分)
            for (int i = 0; i < feedbacks.size(); i++) {
                AqiFeedback f = (AqiFeedback)feedbacks.get(i);
                byte[] keyBytes = serializer.serialize(FeedbackRedisPrefix.FEEDBACK_LIST +  f.getTelId());
                //转成JSONString存储
                byte[] valueBytes = serializer.serialize(f.getAfId().toString());
                connection.zAdd(keyBytes, f.getTimestamp(),valueBytes);
                redisUtil.hset(FeedbackRedisPrefix.FEEDBACK_DATA, String.valueOf(f.getAfId()), f);
            }
            return null;
        });
    }


        public List<AqiFeedback> findFeedbackByPage (String telId,int page, int size){
            long start = (page-1) * size;
            long end = start + size - 1;
            Set<Object> ids = redisUtil.zRange(FeedbackRedisPrefix.FEEDBACK_LIST + telId, start, end);
            if (CollectionUtils.isEmpty(ids)) {
                return null;
            }
            return ids.stream().map(id -> (AqiFeedback) redisUtil.hget(FeedbackRedisPrefix.FEEDBACK_DATA, String.valueOf(id))).collect(Collectors.toList());
        }

        public long countFeedback (String telId){
            return redisUtil.zCard(FeedbackRedisPrefix.FEEDBACK_LIST + telId);
        }


}
