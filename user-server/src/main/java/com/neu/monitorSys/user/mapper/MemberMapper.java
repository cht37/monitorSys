package com.neu.monitorSys.user.mapper;

import com.neu.monitorSys.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.monitorSys.entity.Roles;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


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
   List<Roles> getRolesByMemberId(Integer memberId);

   Integer getRoleIdByLogId(String logId);
}
