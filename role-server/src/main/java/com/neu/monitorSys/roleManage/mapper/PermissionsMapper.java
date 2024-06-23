package com.neu.monitorSys.roleManage.mapper;

import com.neu.monitorSys.entity.Permissions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-19
 */
@Mapper
public interface PermissionsMapper extends BaseMapper<Permissions> {
       List<Permissions> selectPermissionsByRoleId(Integer roleId);
}
