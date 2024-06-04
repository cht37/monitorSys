package com.neu.monitorSys.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.neu.monitorSys.user.entity.Member;
import com.neu.monitorSys.user.DTO.MemberWithRole;
import com.neu.monitorSys.user.mapper.MemberMapper;
import com.neu.monitorSys.user.service.IMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.user.util.RedisUtil;
import com.neu.monitorSys.user.constants.UserRedisPrefix;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表	 服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {
        @Resource
        private MemberMapper memberMapper;
        @Resource
        private RedisUtil redisUtil;


        @Override
        public boolean updateMember(Member member) {
            //首先
            return false;
        }

        @Override
        public MemberWithRole getMemberWithRole(String logId) {
            //首先，从redis中获取用户信息
            MemberWithRole memberWithRole = (MemberWithRole) redisUtil.get(logId);
            if(ObjectUtil.isEmpty(memberWithRole)){
                //如果redis中没有用户信息，则从数据库中获取
                memberWithRole = memberMapper.selectMemberWithRole(logId);
                //将用户信息存入redis，有效期为5小时
                redisUtil.set(UserRedisPrefix.USER_PREFIX +logId, JSONUtil.toJsonStr(memberWithRole), 60*60*5);
            }
            return memberWithRole;
        }
}
