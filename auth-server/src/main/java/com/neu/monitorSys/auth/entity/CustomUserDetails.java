package com.neu.monitorSys.auth.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.neu.monitorSys.common.entity.Permissions;
import com.neu.monitorSys.common.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private SysUser sysUser;
    private List<String> permissions;
    private List<Permissions> permissionsList;
    //存储SpringSecurity所需要的权限信息的集合
    @JSONField(serialize = false)
    private List<GrantedAuthority> authorities;

    public List<String> getPermissions() {
        return permissions;
    }

    public CustomUserDetails(SysUser user, List<String> arrays, List<Permissions> menuTreeRecursion) {
        this.sysUser = user;
        this.permissions = arrays;
        this.permissionsList = menuTreeRecursion;
    }
     public CustomUserDetails(SysUser user, List<String> permissions) {
        this.sysUser = user;
        this.permissions = permissions;
    }
    // TODO 权限字段
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities != null) {
            return authorities;
        }
        if (permissions == null) {
            authorities = new ArrayList<>();
            return authorities;
        }
        // 把permissions中字符串类型的权限信息转换成GrantedAuthority对象存入authorities中
        authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // 用户登录 无权限校验
        return authorities;

    }

    @Override
    public String getPassword() {
        return sysUser.getPassword();
    }

    @Override
    public String getUsername() {
        return sysUser.getUserName();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
