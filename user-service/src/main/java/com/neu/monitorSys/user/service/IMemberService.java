package com.neu.monitorSys.user.service;

import com.neu.monitorSys.user.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neu.monitorSys.user.DTO.MemberWithRole;

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
}
