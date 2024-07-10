package com.neu.monitor_sys.user.repository;

import cn.hutool.json.JSONUtil;
import com.neu.monitor_sys.common.DTO.MemberWithRole;
import com.neu.monitor_sys.common.constants.UserRedisPrefix;
import com.neu.monitor_sys.common.entity.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class UserDetailRepository {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 保存用户详细信息
     * @param logId 用户id
     * @param memberWithRole 用户详细信息
     * @return 是否保存成功
     */
    public boolean saveUserDetail(String logId, MemberWithRole memberWithRole, long ttl) {
        //获取角色列表，将用户索引按照角色存入redis
        List<Roles> roles = memberWithRole.getRoles();
        //用户角色索引用STING存储，因为没有排序的需求
        for (Roles role : roles) {
            redisTemplate.opsForSet().add(UserRedisPrefix.USER_ROLE_PREFIX + role.getId(), logId);
        }
        //将用户信息存入redis
        redisTemplate.opsForValue().set(UserRedisPrefix.USER_DETAIL_PREFIX + logId, JSONUtil.toJsonStr(memberWithRole), ttl, TimeUnit.SECONDS);
        return true;
    }

}
