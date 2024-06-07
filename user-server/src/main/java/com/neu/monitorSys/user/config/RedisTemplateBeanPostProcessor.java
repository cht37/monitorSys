package com.neu.monitorSys.user.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
/**
 * RedisTemplate序列化配置
 * 解决redis可视化乱码问题
 * 由于redis默认编码方式与序列化方式不同，所以需要自定义序列化方式
 */
@Configuration
public class RedisTemplateBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(RedisTemplate.class.isAssignableFrom(bean.getClass())) {
            RedisTemplate redisTemplate = (RedisTemplate)bean;
            StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
            redisTemplate.setKeySerializer(stringRedisSerializer);
            redisTemplate.setValueSerializer(stringRedisSerializer);
        }
        return bean;
    }
}