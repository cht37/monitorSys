package com.neu.monitorSys.auth.mapper;

import com.neu.monitorSys.auth.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表	 Mapper 接口
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-01
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {

}
