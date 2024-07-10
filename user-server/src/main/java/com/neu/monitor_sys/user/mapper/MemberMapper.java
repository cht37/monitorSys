package com.neu.monitor_sys.user.mapper;

import com.neu.monitor_sys.common.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.monitor_sys.common.entity.Roles;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

   List<Integer> getRoleIdByLogId(String logId);
   @Update("update sys_user set state =#{param1} where user_name = #{param2}")
   boolean disableSysUser(@Param("param1") Integer param1,@Param("param2") String param2);

   @Select("select state from sys_user where user_name = #{param1}")
   Integer getSysState(@Param("param1")String logId);
}
