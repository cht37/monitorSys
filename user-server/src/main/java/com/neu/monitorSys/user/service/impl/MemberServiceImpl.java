package com.neu.monitorSys.user.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.common.entity.Member;
import com.neu.monitorSys.common.entity.Permissions;
import com.neu.monitorSys.common.entity.Roles;
import com.neu.monitorSys.common.DTO.MemberWithRole;
import com.neu.monitorSys.user.DTO.MemberSearchDTO;
import com.neu.monitorSys.user.client.RoleClient;
import com.neu.monitorSys.common.constants.UserRedisPrefix;
import com.neu.monitorSys.user.mapper.MemberMapper;
import com.neu.monitorSys.user.repository.UserDetailRepository;
import com.neu.monitorSys.user.service.IMemberService;
import com.neu.monitorSys.user.util.RedisUtil;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private RoleClient roleClient;

    @Autowired
    private UserDetailRepository userDetailRepository;


    @Override
    @GlobalTransactional
    public boolean updateMember(Member member) {
        //去掉id
        member.setId(null);
        //去掉logid
        String logId=member.getLogid();
        member.setLogid(null);
        //去掉tel
        member.setTel(null);
        //去掉state
        member.setState(null);
        //去掉isNew
        member.setIsNew(null);
        //首先，更新数据库中的用户信息
        UpdateWrapper<Member> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("logid",logId);
        int update = memberMapper.update(member, updateWrapper);
        //然后，删除redis中的用户信息
        redisUtil.del(UserRedisPrefix.USER_PREFIX + member.getLogid());
        return update > 0;
    }

    @Override
    public MemberWithRole getMemberWithRole(String logId) {
        //从redis直接获取用户角色信息
        String  memberWithRoleJson = StrUtil.toStringOrNull(redisUtil.get(UserRedisPrefix.USER_ROLE_PREFIX + logId));
        if (memberWithRoleJson != null&&!memberWithRoleJson.equals("")) {
            return JSONUtil.toBean(memberWithRoleJson, MemberWithRole.class);
        }
        //该格式缓存停止使用
        /*
        //首先，从redis中获取用户信息
        String jsonStr = StrUtil.toStringOrNull(redisUtil.get(UserRedisPrefix.USER_PREFIX + logId));
         Member member=null;
        if (jsonStr != null) {
            member = JSONUtil.toBean(jsonStr, Member.class);
        }
        //如果member为空，则从数据库中获取
        if (!ObjectUtil.isNotNull(member)) {
            member = getMember(logId);
        }
         */
        //从数据库中获取用户信息
        Member member = getMember(logId);
        //获取memberId
        Integer memberId = member.getId();
        //获取用户角色列表
        List<Roles> roles = memberMapper.getRolesByMemberId(memberId);
        //根据角色id获取权限名称列表
        Set<String> permissions=new HashSet<>();
       //遍历角色集合
        for (Roles role : roles) {
            List<Permissions> permissionsByRoleId = roleClient.getPermissionsByRoleId(role.getId()).getData();
           //由于权限有子权限，需要dfs遍历
           addPermissions(permissionsByRoleId,permissions);
        }
        MemberWithRole memberWithRole = new MemberWithRole();
        memberWithRole.setMember(member);
        memberWithRole.setRoles(roles);
        memberWithRole.setPermissions(permissions);
        String rolesString = roles.toString();
        //将用户信息存入redis，有效期为5小时
//        redisUtil.set(UserRedisPrefix.USER_ROLE_PREFIX+":"+rolesString+":" + logId, JSONUtil.toJsonStr(memberWithRole), 60 * 60 * 5);
        userDetailRepository.saveUserDetail(logId,memberWithRole,60*60*5);
        return memberWithRole;
    }
    public void addPermissions(List<Permissions> permissionsList,Set<String> permissions){
        for(Permissions permission:permissionsList){
            permissions.add(permission.getPermissionName());
            if (permission.getChildSysMenu()!=null&&permission.getChildSysMenu().size()>0){
                addPermissions(permission.getChildSysMenu(),permissions);
            }
        }
    }
    @Override
    public IPage<MemberWithRole> selectMembers(MemberSearchDTO memberSearchDTO, Integer page, Integer size) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(memberSearchDTO.getLogId() != null, Member::getLogid, memberSearchDTO.getLogId())
                .like(StrUtil.isNotBlank(memberSearchDTO.getName()), Member::getMname, memberSearchDTO.getName())
                .eq(StrUtil.isNotBlank(memberSearchDTO.getMobile()), Member::getTel, memberSearchDTO.getMobile())
                .eq(memberSearchDTO.getGender() != null, Member::getGender, memberSearchDTO.getGender())
                .eq(memberSearchDTO.getState() != null, Member::getState, memberSearchDTO.getState());
        if (memberSearchDTO.getRoles()!=null&&!memberSearchDTO.getRoles().equals("")) {
            List<String> memberIds = roleClient.getLogIdByRoleName(memberSearchDTO.getRoles()).getData();
            if(memberIds!=null&&memberIds.size()>0){
                wrapper.in(Member::getLogid, memberIds);
            }else {
                return new Page<>();
            }
        }
        IPage<Member> memberIPage = memberMapper.selectPage(new Page<>(page, size), wrapper);
        List<Member> members = memberIPage.getRecords();
//        List<Member> members = memberMapper.selectList(wrapper);
        List<MemberWithRole> memberWithRoles = new ArrayList<>();
        members.forEach(member -> {
            MemberWithRole memberWithRole = getMemberWithRole(member.getLogid());
            memberWithRoles.add(memberWithRole);
        });
        IPage<MemberWithRole> memberWithRoleIPage = new Page<>();
        memberWithRoleIPage.setRecords(memberWithRoles);
        memberWithRoleIPage.setTotal(memberIPage.getTotal());
        memberWithRoleIPage.setCurrent(memberIPage.getCurrent());
        memberWithRoleIPage.setSize(memberIPage.getSize());
        return memberWithRoleIPage;
    }

    @Override
    public Member getMember(String logId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getLogid, logId);
        return memberMapper.selectOne(wrapper);
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

    @Override
    public String getNameById(String memberId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getLogid, memberId);
        Member member = memberMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(member)) {
            throw new RuntimeException("用户不存在");
        }
        return member.getMname();
    }

    @Override
    public List<Integer> getRoleIdByLogId(String logId) {
        return memberMapper.getRoleIdByLogId(logId);
    }

    @Override
    @Transactional
    public void saveMember(Member member) {
        int i = 0;
        try {
            i = memberMapper.insert(member);
        } catch (Exception e) {
            throw new RuntimeException("服务器异常");
        }
        if(i<=0){
            throw new RuntimeException("新增用户失败");
        }
    }

    @Override
    public boolean isNewMember(String logId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getLogid, logId);
        Member member = memberMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(member)) {
            throw new RuntimeException("用户不存在");
        }
        return member.getIsNew() == 1;
    }

    @Override
    @Transactional
    public boolean setIsNew(String logId) {
        UpdateWrapper<Member> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("logid", logId);
        Member member = new Member();
        member.setIsNew(0);
        int update = memberMapper.update(member, updateWrapper);
        if (update <= 0) {
            throw new RuntimeException("设置失败");
        }
        //删除redis缓存
        redisUtil.del(UserRedisPrefix.USER_ROLE_PREFIX + logId);
        return true;
    }

    @Override
    public boolean disableMember(String logId, String currentLogId) {
        if (logId.equals(currentLogId)) {
            throw new RuntimeException("不能禁用自己");
        }
        boolean updated=memberMapper.disableSysUser(0,logId);
        if (!updated) {
            throw new RuntimeException("禁用失败");
        }
        return true;
    }

    @Override
    public boolean enableMember(String logId, String currentLogId) {
        if (logId.equals(currentLogId)) {
            throw new RuntimeException("不能启用自己");
        }
        boolean updated=memberMapper.disableSysUser(1,logId);
        if (!updated) {
            throw new RuntimeException("启用失败");
        }
        return true;
    }


}
