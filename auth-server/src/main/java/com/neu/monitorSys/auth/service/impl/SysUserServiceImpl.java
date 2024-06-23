package com.neu.monitorSys.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.auth.client.RoleClient;
import com.neu.monitorSys.auth.client.UserClient;
import com.neu.monitorSys.auth.constants.AuthRedisPrefix;
import com.neu.monitorSys.auth.service.SysUserService;
import com.neu.monitorSys.auth.utils.RedisUtil;
import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.entity.DTO.SysUserDTO;
import com.neu.monitorSys.entity.Member;
import com.neu.monitorSys.auth.mapper.SysUserMapper;
import com.neu.monitorSys.entity.SysUser;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper,SysUser> implements SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RoleClient roleClient;

    @Autowired
    private RedisUtil redisUtil;
    @Override
    public SysUser getUser(String userName) {
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_name",userName);
        return sysUserMapper.selectOne(queryWrapper);

    }

    @Override
    public SysUser getUserByMobile(String mobile) {
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_tel",mobile);
        return sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 注册
     * @param userDTO 用户密码信息
     * @return 是否注册成功
     */
    @Override
    @GlobalTransactional
    public boolean savePublicUser(SysUserDTO userDTO) {
        SysUser sysUser=new SysUser();
       //自动生成用户名
        sysUser.setUserName(generateUsername());
        try {
            //校验手机号和验证码
            checkUser(userDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //校验账号是否存在
        if (getUser(sysUser.getUserName())!=null){
           sysUser.setUserName(generateUsername());
        }
        //判断手机号是否存在
        if (getUserByMobile(userDTO.getMobile())!=null){
            throw new RuntimeException("手机号已存在");
        }
        sysUser.setUserTel(userDTO.getMobile());
//        sysUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        //默认设置密码为空
        sysUser.setPassword("");
        int i =0;
        try {
            i = sysUserMapper.insert(sysUser);
        } catch (Exception e) {
            throw new RuntimeException("注册异常");
        }
        //如果新增用户成功，写入默认的publicMember
        boolean memberSaved=false;
        if(i>0){
            Member member=new Member();
            member.setLogid(sysUser.getUserName());
            MyResponse<Boolean> memberResponse = userClient.saveMember(member);
            if(memberResponse.getData()){
                memberSaved=true;
            }else {
                throw new RuntimeException(memberResponse.getMessage());
            }
        }else {
            throw new RuntimeException("服务器异常");
        }
        //如果新增用户和member都成功，则继续设置默认角色（“public"）
        log.info(String.valueOf(sysUser.getId()));
        MyResponse<Boolean> response = roleClient.setDefaultRole(sysUser.getId());
        if(response.getData()){
            return true;
        }else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * 校验用户信息
     * @param userDTO 用户信息
     */
    private void checkUser(SysUserDTO userDTO) {
        if(userDTO.getSmsCode()==null||userDTO.getMobile()==null){
            throw new RuntimeException("手机号或验证码不能为空");
        }
        //手机号格式校验
        if(!userDTO.getMobile().matches("^1[3-9]\\d{9}$")){
            throw new RuntimeException("手机号格式不正确");
        }
        //验证码校验
        if(userDTO.getSmsCode().length()!=6){
            throw new RuntimeException("验证码格式不正确");
        }
        String sms = redisUtil.get(AuthRedisPrefix.SMS_PREFIX + userDTO.getMobile()).toString();
        if(sms==null){
            throw new RuntimeException("验证码已过期");
        } else if (!sms.equals(userDTO.getSmsCode())){
            throw new RuntimeException("验证码错误");
        }

    }
     /**
     * 根据UUID取hash值+随机数，产生命令唯一标识
     *
     * @throws InterruptedException
     */
    public static String generateUsername() {
        String orderSeq = Math.abs(UUID.randomUUID().toString().hashCode()) + "";
        while (orderSeq.length() < 13) {
            orderSeq = orderSeq + (int) (Math.random() * 10);
        }
        return orderSeq;
    }
}