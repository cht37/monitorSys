package com.neu.monitorSys.user.constants;

import lombok.Getter;

// 该注解需要添加lombok
@Getter
public enum ResultCode {

    SUCCESS(1000, "操作成功"),

    FAILED(1001, "响应失败"),

    VALIDATE_FAILED(1002, "参数校验失败"),

    NO_RESULT(1003, "未查询到相关信息"),

    MES_ERROR(1004, "未查询到相关信息"),

    ERROR(5000, "未知错误"),

    FILE_UPLOAD_ERROR(5001, "文件上传失败");


    private int code;
    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
