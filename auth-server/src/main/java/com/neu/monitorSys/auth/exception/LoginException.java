package com.neu.monitorSys.auth.exception;

public class LoginException extends RuntimeException{
     /**
     * 异常码
     */
    private final int code;

    private final String message;

    public LoginException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }




}
