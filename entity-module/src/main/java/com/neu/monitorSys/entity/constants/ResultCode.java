package com.neu.monitorSys.entity.constants;

import lombok.Getter;

// 该注解需要添加lombok
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),

    BAD_REQUEST(400, "请求参数错误"),

    UNAUTHORIZED(401, "未授权"),

    FORBIDDEN(403, "禁止访问"),

    NOT_FOUND(404, "未找到资源"),

    VALIDATE_FAILED(422, "参数校验失败"),

    SERVER_ERROR(500, "服务器内部错误"),

    NO_RESULT(204, "未查询到相关信息"),

    FAILED(5000, "操作失败"),

    FILE_UPLOAD_ERROR(5001, "文件上传失败"),

    MES_ERROR(5002, "消息处理错误");


    private int code;
    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
