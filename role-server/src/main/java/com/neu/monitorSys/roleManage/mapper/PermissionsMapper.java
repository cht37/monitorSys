package com.neu.monitorSys.roleManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.monitorSys.common.entity.Permissions;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-19
 */
@Mapper
public interface PermissionsMapper extends BaseMapper<Permissions> {
    List<Permissions> selectPermissionsByRoleId(Integer roleId);

    @Delete("delete from role_permission where role_id = #{roleId}")
    void deletePermissionsByRoleId(Integer roleId);

    void addPermissions(@Param("param1") Integer roleId, @Param("param2") Integer[] permissionsIds);
}
