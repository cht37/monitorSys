package com.neu.monitorSys.user.mapper;

import com.neu.monitorSys.user.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.monitorSys.user.DTO.MemberWithRole;

/**
 * <p>
 * 用户表	 Mapper 接口
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
public interface MemberMapper extends BaseMapper<Member> {
   MemberWithRole selectMemberWithRole(String logId);
}
