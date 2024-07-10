package com.neu.monitor_sys.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitor_sys.common.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neu.monitor_sys.common.DTO.MemberWithRole;
import com.neu.monitor_sys.user.DTO.MemberSearchDTO;

import java.util.List;

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
     * 查询用户信息
     */
    IPage<MemberWithRole> selectMembers(MemberSearchDTO memberSearchDTO, Integer page, Integer size);
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
    List<Integer> getRoleIdByLogId(String logId);

    /**
     * 新增用户Member
     */
    void saveMember(Member member);

    /**
     * 是否是新用户
     * @param logId 用户登录id
     * @return 是否是新用户
     */
    boolean isNewMember(String logId);

    /**
     * 设置为不是新用户
     * @param logId 用户登录id
     * @return 是否设置成功
     */
    boolean setIsNew(String logId);

    /**
     * 禁用用户
     */
    boolean disableMember(String logId,String currentLogId);
    /**
     * 启用用户
     */
    boolean enableMember(String logId,String currentLogId);

    /**
     * 设置用户状态
     */
    boolean setState(String logId, Integer state);

}
