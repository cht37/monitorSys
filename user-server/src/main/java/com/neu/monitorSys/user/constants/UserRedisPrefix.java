package com.neu.monitorSys.user.constants;

public class UserRedisPrefix {
    //已登录用户基本信息
    public static final String USER_PREFIX = "login:member:";
    //已登录用户带权限的基本信息
    public static final String USER_ROLE_PREFIX = "login:member_with_role:";
    // 网格员部分信息
    public static final String GM_INFO_PREFIX = "gm:info:";
    // 全体网格员缓存信息
    public static final String GM_ALL_PREFIX = "gm:all:";
}
