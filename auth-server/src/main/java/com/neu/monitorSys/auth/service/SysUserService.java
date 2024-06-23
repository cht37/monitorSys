package com.neu.monitorSys.auth.service;

import com.neu.monitorSys.entity.DTO.SysUserDTO;
import com.neu.monitorSys.entity.Member;
import com.neu.monitorSys.entity.SysUser;


public interface SysUserService {
    /**
     * 根据用户名获取Sys用户
     * @param userName
     * @return
     */
    SysUser getUser(String userName);

    /**
     * 根据手机号获取Sys用户
     * @param mobile
     * @return
     */
    SysUser getUserByMobile(String mobile);

    /**
     * 注册
     * @param userDTO 用户密码信息
     * @return
     */
    boolean savePublicUser(SysUserDTO userDTO);

}
