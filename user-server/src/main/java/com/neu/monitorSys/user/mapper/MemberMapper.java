package com.neu.monitorSys.user.mapper;

import com.neu.monitorSys.user.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.monitorSys.user.DTO.MemberWithRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;


/**
 * <p>
 * 用户表	 Mapper 接口
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {
   MemberWithRole selectMemberWithRole(String logId);
}
