package com.neu.monitorSys.roleManage.mapper;

import com.neu.monitorSys.entity.Roles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.monitorSys.roleManage.VO.PermissionRoleMapVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
