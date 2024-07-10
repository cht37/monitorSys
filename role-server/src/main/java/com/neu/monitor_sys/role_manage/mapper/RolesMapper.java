package com.neu.monitor_sys.role_manage.mapper;

import com.neu.monitor_sys.common.entity.Roles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.monitor_sys.role_manage.VO.PermissionRoleMapVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-07
 */
@Mapper
public interface RolesMapper extends BaseMapper<Roles> {

    List<PermissionRoleMapVO> getRoleListByPermissionUrl();

    @Insert("insert into user_role (user_id, role_id) values(#{param1}, #{param2})")
    void addUserRole(Integer userId,  Integer roleId) throws Exception;
    @Delete("delete from user_role where user_id in(select id from sys_user where user_name = #{param1})")
    void deleteUserRole(@Param("param1") String param1);
    @Insert("insert into user_role (user_id, role_id) values((select id from sys_user where user_name = #{param1}), #{param2})")
    void addUserRoleByLogId(String logId,Integer roleId);

    List<String> getLogIdByRoleNames(@Param("roleNameList") List<String> roleNameList);

    List<String> getLogIdByRoleName(String roleName);

    List<String> getRoleNameByPermissionId(@Param("clue") List<Integer> clue);
}
