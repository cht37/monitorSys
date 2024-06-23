package com.neu.monitorSys.user.service;

import com.neu.monitorSys.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neu.monitorSys.entity.DTO.MemberWithRole;

/**
 * <p>
 * 用户表	 服务类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
public interface IMemberService extends IService<Member> {
    /**
     * 修改用户信息
     */
    boolean updateMember(Member member);
    /**
     * 获取带有角色名称的用户基本信息
     */
    MemberWithRole getMemberWithRole(String logId);

    /**
     * 获取用户基本信息
     */
    Member getMember(String logId);


    /**
     * 根据手机号获取用户基本信息
     */
    Member getMemberByMobile(String mobile);

    /**
     * 根据id获取用户姓名
     */
    String getNameById(String memberId);

    /**
     * 根据登录id获取角色id
     */
    Integer getRoleIdByLogId(String logId);

    /**
     * 新增用户Member
     */
    void saveMember(Member member);


}
