package com.neu.monitorSys.auth.constants;

public class AuthRedisPrefix {
        /**
         * token前缀
         */
        public static final String AUTH_PREFIX = "login:token:";
        /**
         * 短信验证码
         */
        public static final String SMS_PREFIX = "sms:";

        /**
         * 自定义userDetail
         */
        public static final String USER_CUSTOM_DETAIL_PREFIX = "login:user_detail:";
        /**
         * 用户基本信息
         */
        public static final String USER_DETAIL_PREFIX = "login:member:";
        /**
         * 用户认证信息
         */
        public static final String USER_AUTHENTICATION_PREFIX="login:authentication:";
        /**
         * 用户角色信息
         */
        public static final String USER_ROLE_PREFIX = "login:member_with_role:";
}
