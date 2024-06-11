package com.neu.monitorSys.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.user.DTO.MemberWithRole;
import com.neu.monitorSys.user.constants.UserRedisPrefix;
import com.neu.monitorSys.user.entity.Member;
import com.neu.monitorSys.user.mapper.MemberMapper;
import com.neu.monitorSys.user.service.IMemberService;
import com.neu.monitorSys.user.util.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public boolean updateMember(Member member) {
        //去除用户信息中的密码
        member.setLogpwd(null);
        //首先，更新数据库中的用户信息
        UpdateWrapper<Member> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("logid", member.getLogid());
        int update = memberMapper.update(member, updateWrapper);
        //然后，删除redis中的用户信息
        redisUtil.del(UserRedisPrefix.USER_PREFIX + member.getLogid());
        return update > 0;
    }

    @Override
    public MemberWithRole getMemberWithRole(String logId) {
        //首先，从redis中获取用户信息
        String jsonStr = StrUtil.toStringOrNull(redisUtil.get(UserRedisPrefix.USER_PREFIX+ logId));
        if (jsonStr != null) {
            return JSONUtil.toBean(jsonStr, MemberWithRole.class);
        }
        //如果redis中没有用户信息，则从数据库中获取
        MemberWithRole memberWithRole = memberMapper.selectMemberWithRole(logId);
        //将用户信息存入redis，有效期为5小时
        redisUtil.set(UserRedisPrefix.USER_PREFIX + logId, JSONUtil.toJsonStr(memberWithRole), 60 * 60 * 5);
        return memberWithRole;
    }

    @Override
    public Member getMember(String logId, String method) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getLogid, logId);
        Member member = memberMapper.selectOne(wrapper);
        if(method==null||method.equals("")){
            member.setLogpwd(null);
            return member;
        }else if("login".equals(method)){
            return member;
        }
        throw new RuntimeException("method参数错误");
    }

    @Override
    public Member getMemberByMobile(String mobile) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getTel, mobile);
        Member member = memberMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(member)) {
            throw new RuntimeException("用户不存在");
        }
        return member;
    }

}
