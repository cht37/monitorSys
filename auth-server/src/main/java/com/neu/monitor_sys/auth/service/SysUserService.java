package com.neu.monitor_sys.auth.service;

import com.neu.monitor_sys.auth.DTO.ModifyPasswordDTO;
import com.neu.monitor_sys.common.DTO.SysUserDTO;
import com.neu.monitor_sys.common.entity.SysUser;


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

    /**
     * 修改密码
     * @param modifyPasswordDTO 修改密码信息
     * @param logId 登录id
     * @return 是否修改成功
     */
    boolean modifyPassword(ModifyPasswordDTO modifyPasswordDTO, String logId);

    /**
     * 保存普通用户
     * @param userDTO 用户信息
     * @return 是否保存成功
     */
    boolean saveNormalUser(SysUserDTO userDTO);
    /**
     * 重置密码
     */
    boolean reversePassword(String logId);
}
