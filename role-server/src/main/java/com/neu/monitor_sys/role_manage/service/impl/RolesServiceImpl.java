package com.neu.monitor_sys.role_manage.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitor_sys.common.DTO.LogoutMsg;
import com.neu.monitor_sys.common.DTO.MyResponse;
import com.neu.monitor_sys.common.constants.AuthRedisPrefix;
import com.neu.monitor_sys.common.constants.UserRedisPrefix;
import com.neu.monitor_sys.common.entity.Permissions;
import com.neu.monitor_sys.common.entity.Roles;
import com.neu.monitor_sys.role_manage.client.AuthClient;
import com.neu.monitor_sys.role_manage.client.UserClient;
import com.neu.monitor_sys.role_manage.config.PermissionUrlConfig;
import com.neu.monitor_sys.role_manage.mapper.RolesMapper;
import com.neu.monitor_sys.role_manage.publisher.MessagePublisher;
import com.neu.monitor_sys.role_manage.service.IPermissionsService;
import com.neu.monitor_sys.role_manage.service.IRolesService;
import com.neu.monitor_sys.role_manage.util.RedisUtil;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-07
 */
@Service
@Slf4j
public class RolesServiceImpl extends ServiceImpl<RolesMapper, Roles> implements IRolesService {
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private RolesMapper rolesMapper;
    @Autowired
    private AuthClient authClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    @Lazy
    private IPermissionsService permissionsService;

    @Autowired
    private MessagePublisher messagePublisher;
    private final PermissionUrlConfig permissionUrlConfig;
    private String logId;
    private List<Integer> roleIds;

    public RolesServiceImpl(PermissionUrlConfig permissionUrlConfig) {
        this.permissionUrlConfig = permissionUrlConfig;
    }

    @Override
    public List selectAllRoles() {
        //查询redis缓存
        Object data = redisUtil.get("roles");
        if (data != null) {
            return JSONUtil.toList(data.toString(), Roles.class);
        }
        //从数据库查询全部角色信息
        List<Roles> roles = rolesMapper.selectList(null);
        if (roles != null) {
            //存入redis缓存，缓存时间为1小时
            redisUtil.set("roles", JSONUtil.toJsonStr(roles), 60 * 60);
            return roles;
        }
        return ListUtil.empty();

    }

    @Override
    @Transactional
    public boolean updateRole(Roles role) {
        //更新角色信息
        role.setEnable(null);
        int result = rolesMapper.updateById(role);
        if (result > 0) {
            //删除redis缓存
            redisUtil.del("roles");
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean addRole(Roles role) {
        //新增角色
        int result = rolesMapper.insert(role);
        if (result > 0) {
            //删除redis缓存
            redisUtil.del("roles");
            return true;
        }
        return false;
    }

    @Override
    public Roles getRoleById(Integer roleId) {
        //从数据库查询角色信息
        return rolesMapper.selectById(roleId);
    }

    @Override
    public List<String> getRoleListByPermissionUrl(String permissionUrl) {
        if (permissionUrl == null || permissionUrl.equals("")) {
            return new ArrayList<>();
        }
        List<String> roles;

        /*
          1.首要原则，判断用户是否有父级权限，直到根节点
          2.如果不是多条件查询，则有以下情况：
           2.1 根据网格id查询反馈信息（GET /api/v1/feedbacks/waiting-list）必须与  网格员是否可指派或是否已经指派给afId反馈（GET /api/v1/grid-managers/available）同时存在
           2.2 修改用户角色（ PUT /api/v1/roles/users/{userId}/roles ）依赖查询全部角色信息（GET /api/v1/roles）
           2.3 获取权限树（全部）（GET /api/v1/permissions/tree）必须与 根据roleId获取权限列表（GET /api/v1/permissions/role/*）同时存在
           2.4 指派网格员（POST /api/v1/feedbacks/assign）依赖 根据条件查询反馈信息（GET /api/v1/feedbacks/search）和 根据条件查询网格员信息 （GET /api/v1/grid-managers/search）
          根据规则，将需要验证的uri或permission_name加入集合，然后进行验证
         */
        Set<Integer> result = new HashSet<>();
        //获取权限列表
        List<Permissions> permissionsList = permissionsService.getAllPermissions();
        String uri = permissionUrl;
        AntPathMatcher matcher = new AntPathMatcher();
        //获取uri
        for (Permissions permission : permissionsList) {
            if (permission.getApi() != null) {
                if (matcher.match(permission.getApi(), permissionUrl)) {
                    uri = permission.getApi();
                }
            }
        }
        //映射HashMap。key为权限url，value为Permissions对象
        // 构建parentId索引哈希表
        Map<Integer, Permissions> permissionMap = new HashMap<>();
        Map<String, Permissions> urlToPermissionMap = new HashMap<>();
        for (Permissions permission : permissionsList) {
            permissionMap.put(permission.getId(), permission);
            if (permission.getApi() != null) {
                urlToPermissionMap.put(permission.getApi(), permission);
            }
        }
        List<Integer> clue = checkPermissionUrl(uri, urlToPermissionMap, permissionMap, result);
        roles = rolesMapper.getRoleNameByPermissionId(clue);

        //获取角色列表后需要进一步校验特殊情况
        return roles;
    }

    @Override
    public List<String> getRoleNamesByUserId(String userId) {
        MyResponse<List<Integer>> response = userClient.getRoleIdByLogId(userId);
        //从数据库查询用户角色名称列表
        if (response.getData() == null) {
            return new ArrayList<>();
        }
        List<Integer> roleIds = response.getData();
        List<Roles> rolesList = rolesMapper.selectList(new QueryWrapper<Roles>().lambda().in(Roles::getId, roleIds));
        List<String> roleNames = new ArrayList<>();
        for (Roles role : rolesList) {
            if (role.getEnable() == 1) {
                roleNames.add(role.getMname());
            }
        }
        return roleNames;
    }

    @Override
    @Transactional
    public void setDefaultRole(Integer userId) {
        // 先判断是否存在默认角色PUBLIC
        List<Roles> roles = rolesMapper.selectList(new QueryWrapper<Roles>().lambda().eq(Roles::getMname, "PUBLIC"));
        Integer roleId;

        if (roles.isEmpty()) {
            // 不存在则新增默认角色PUBLIC
            Roles role = new Roles();
            role.setMname("PUBLIC");
            rolesMapper.insert(role);
            // 重新获取默认角色PUBLIC的id
            roleId = role.getId();
        } else {
            // 获取默认角色PUBLIC的id
            roleId = roles.get(0).getId();
        }

        // 设置用户角色
        try {
            rolesMapper.addUserRole(userId, roleId);
        } catch (Exception e) {
            throw new RuntimeException("分配角色异常" + e.getMessage());
        }
    }

    /**
     * 修改用户角色
     *
     * @param logId   用户id
     * @param roleIds 角色id列表
     * @return 是否修改成功
     */
    @Override
    @GlobalTransactional
    public boolean updateUserRole(String logId, List<Integer> roleIds) {
        //查询用户原来的角色
        List<Integer> oldRoleIds = userClient.getRoleIdByLogId(logId).getData();
        boolean isGridManager = false;
        //判断用户原来是否有网格员角色
        if (oldRoleIds != null && oldRoleIds.size() > 0) {
            for (Integer oldRoleId : oldRoleIds) {
                if (oldRoleId == 2) {
                    isGridManager = true;
                }
            }
        }
        // 先删除用户所有角色
        rolesMapper.deleteUserRole(logId);
        // 判断是否有角色
        if (roleIds == null || roleIds.isEmpty()) {
            //设置用户状态为0
            userClient.setState(logId, 0);
            return true;
        }
        if (oldRoleIds != null && oldRoleIds.isEmpty()) {
            userClient.setState(logId, 1);
        }
        //判断角色是否存在
        List<Roles> roles = rolesMapper.selectList(new QueryWrapper<Roles>().lambda().in(Roles::getId, roleIds));
        if (roles.size() != roleIds.size()) {
            throw new RuntimeException("角色不存在");
        }
        // 重新分配角色
        boolean hasGridManager = false;
        for (Integer roleId : roleIds) {
            rolesMapper.addUserRoleByLogId(logId, roleId);
            //如果roleId有网格员且原来没有网格员角色，在GridManager表中新增一条记录
            if (roleId == 2 && !isGridManager) {
                userClient.addGridManager(logId);
            }
            if (roleId == 2) {
                hasGridManager = true;
            }
        }
        //如果原来有网格员角色且现在没有网格员角色，删除GridManager表中的记录
        if (isGridManager && !hasGridManager) {
            userClient.deleteGridManager(logId);
        }

        //判断用户是否登录
        if (redisUtil.hasKey(AuthRedisPrefix.AUTH_PREFIX + logId)) {
            String token = redisUtil.get(AuthRedisPrefix.AUTH_PREFIX + logId).toString();
            MyResponse<String> logout = authClient.logout(logId, token);
            if (logout.getStatusCode() != 200) {
                throw new RuntimeException("发送消息通知用户退出登录失败");
            }
            //发布消息到消息队列，通知用户登录已经失效
            messagePublisher.sendLogoutMessage(new LogoutMsg(logId, "用户角色已修改，请重新登录"));
        }
        //删除原有角色索引缓存
        if (oldRoleIds != null) {
            for (Integer roleId : oldRoleIds) {
                redisUtil.setRemove(UserRedisPrefix.USER_ROLE_PREFIX + roleId, logId);
            }
        }
        return true;
    }

    @Override
    public List<String> getLogIdByRoleName(String roleNames) {
        List<String> logIds = new ArrayList<>();
        //判断角色名是否为空
        if (roleNames == null || roleNames.equals("")) {
            return logIds;
        }
        //判断角色名是否为逗号分隔
        if (roleNames.contains(",")) {
            String[] roleNameList = roleNames.split(",");
            ArrayList<String> list = ListUtil.toList(roleNameList);
            logIds = rolesMapper.getLogIdByRoleNames(list);
        } else {
            logIds = rolesMapper.getLogIdByRoleName(roleNames);
        }
        return logIds;
    }

    @Override
    public boolean isRoleEnabled(Integer roleId) {
        Roles role = rolesMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        return role.getEnable() == 1;
    }

    @Override
    @Transactional
    public boolean enableRole(Integer roleId, boolean enabled) {
        Roles role = new Roles();
        role.setId(roleId);
        role.setEnable(enabled ? 1 : 0);
        int result = rolesMapper.updateById(role);
        //删除缓存
        redisUtil.del("roles");
        return result > 0;
    }

    /*
       1.首要原则，判断用户是否有父级权限，直到根节点
       2.如果不是多条件查询，则有以下情况：
        2.1 根据网格id查询反馈信息（GET /api/v1/feedbacks/waiting-list）必须与  网格员是否可指派或是否已经指派给afId反馈（GET /api/v1/grid-managers/available）同时存在
        2.2 修改用户角色（ PUT /api/v1/roles/users/{userId}/roles ）依赖查询全部角色信息（GET /api/v1/roles）
        2.3 获取权限树（全部）（GET /api/v1/permissions/tree）必须与 根据roleId获取权限列表（GET /api/v1/permissions/role/*）同时存在
        2.4 指派网格员（POST /api/v1/feedbacks/assign）依赖 根据条件查询反馈信息（GET /api/v1/feedbacks/search）和 根据条件查询网格员信息 （GET /api/v1/grid-managers/search）
       根据规则，将需要验证的uri或permission_name加入集合，然后进行验证
      */
    public List<Integer> checkPermissionUrl(String permissionUrl, Map<String, Permissions> urlToPermissionMap, Map<Integer, Permissions> permissionMap, Set<Integer> result) {
        // 获取当前URL对应的权限
        Permissions currentPermission = urlToPermissionMap.get(permissionUrl);

        // 检查当前URL对应的权限及其依赖的权限
        addRequiredPermissions(permissionUrl, result, urlToPermissionMap, permissionMap);

        // 如果当前权限没有父级权限，直接返回
        if (currentPermission.getParentId() == 0) {
            return new ArrayList<>(result);
        }

        while (currentPermission.getParentId() != 0) {
            currentPermission = permissionMap.get(currentPermission.getParentId());

            // 如果父级权限的API为空，直接添加其ID
            if (currentPermission.getApi() == null) {
                result.add(currentPermission.getId());
            } else {
                // 递归检查父级权限及其依赖的权限
                addRequiredPermissions(currentPermission.getApi(), result, urlToPermissionMap, permissionMap);
            }
        }

        return new ArrayList<>(result);
    }

    /**
     * 递归添加当前URL及其依赖的权限
     *
     * @param url                当前URL
     * @param result             结果集
     * @param urlToPermissionMap URL到权限对象的映射
     * @param permissionMap      权限ID到权限对象的映射
     */
    private void addRequiredPermissions(String url, Set<Integer> result, Map<String, Permissions> urlToPermissionMap, Map<Integer, Permissions> permissionMap) {
        // 添加当前URL到结果集
        result.add(urlToPermissionMap.get(url).getId());

        // 打印依赖项配置
        log.info(permissionUrlConfig.getDependencies().toString());
        url = url.replace("/", "").replace(" ", "");
        // 保持URL的原始格式，不移除斜杠和空格
        if (permissionUrlConfig.getDependencies().containsKey(url)) {
            for (String dependency : permissionUrlConfig.getDependencies().get(url)) {
                if (result.contains(urlToPermissionMap.get(dependency).getId())) {
                    continue;
                }
                // 递归检查依赖的URL
                checkPermissionUrl(dependency, urlToPermissionMap, permissionMap, result);
            }
        }
    }

}
