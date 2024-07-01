package com.neu.monitorSys.auth.service.impl;


import com.neu.monitorSys.auth.client.RoleClient;
import com.neu.monitorSys.auth.entity.CustomUserDetails;
import com.neu.monitorSys.auth.service.SysUserService;
import com.neu.monitorSys.common.entity.Permissions;
import com.neu.monitorSys.common.entity.SysUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private RoleClient roleClient;

    @Override
    public UserDetails loadUserByUsername(String logId) throws UsernameNotFoundException {
        SysUser user = null;
        //先尝试用手机号获取用户
        user = sysUserService.getUserByMobile(logId);
        //如果用户不为空，说明用户输入的是手机号，不需要验证密码是否存在
        if (user == null) {
            user = sysUserService.getUser(logId);
            //如果用户密码为空，说明用户没有输入密码，拒绝访问
            if (user == null) {
            throw new InternalAuthenticationServiceException("用户不存在");
        }
            if (user.getPassword() == null||user.getPassword().equals("")) {
                throw new InternalAuthenticationServiceException("User has no password set");
            }
        }
        //如果用户state为0，说明用户被禁用，拒绝访问
        if (user.getState() == 0) {
            throw new InternalAuthenticationServiceException("User is disabled");
        }
        //获取用户角色
        List<String> roles = roleClient.getRoleNamesByUserId(user.getUserName()).getData();
//        List<Permissions> permissions = BeanUtil.copyToList(list, Permissions.class);
        //设置用户权限信息



        return new CustomUserDetails(user, roles);
    }

    //todo 后面用一下
    public static List<Permissions> getMenuTreeRecursion(List<Permissions> permissions) {
        /**
         * 过滤分出父级菜单和子级菜单
         */
        List<Permissions> parentSysMenuList = permissions.stream().filter(sysMenu -> sysMenu.getParentId() == 0).collect(Collectors.toList());
        List<Permissions> childSysMenuList = permissions.stream().filter(sysMenu -> sysMenu.getParentId() > 0).toList();
        /**
         * 将子级目录菜单转换为map对象
         */
        Map<Integer, List<Permissions>> map = childSysMenuList.stream().collect(Collectors.toMap(Permissions::getId,
                // 此时的value 为集合，方便重复时操作
                s -> {
                    List<Permissions> childSysMenuMap = new ArrayList<>();
                    childSysMenuMap.add(s);
                    return childSysMenuMap;
                },
                // 重复时将现在的值全部加入到之前的值内
                (List<Permissions> value1, List<Permissions> value2) -> {
                    value1.addAll(value2);
                    return value1;
                }
        ));

        /**
         * 循环对比，父级菜单和子级菜单，相同则加入父级对象中
         */
        parentSysMenuList.forEach(e -> {
            List<Permissions> childSysMenus = map.get(e.getId());
            e.setChildSysMenu(childSysMenus);
        });
        /**
         * 需要对返回结果集排序，前端要展示第一个菜单项，做重定向
         */
        parentSysMenuList.sort(Comparator.comparing(Permissions::getOrderNum));
        System.out.println(parentSysMenuList);
        return parentSysMenuList;
    }

}