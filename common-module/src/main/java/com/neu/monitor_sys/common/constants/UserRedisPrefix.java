package com.neu.monitor_sys.common.constants;

public class UserRedisPrefix {
    //已登录用户基本信息
    public static final String USER_PREFIX = "login:member:";
    //已登录用户角色信息索引
    public static final String USER_ROLE_PREFIX = "login:member_with_role:role:";
    //已登录用户详细信息
    public static final String USER_DETAIL_PREFIX = "login:member_with_role:detail:";

    // 网格员部分信息
    public static final String GM_INFO_PREFIX = "gm:info:";
    // 全体网格员缓存信息
    public static final String GM_ALL_PREFIX = "gm:all:";
}
