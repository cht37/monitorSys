package com.neu.monitorSys.gateway.DTO;

public class Response<T> {
	// 状态码
    private int statusCode;
    // 响应信息提示
    private String message;
    // 响应数据
    private T data;

    // 构造函数
    public Response(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
     // 无参构造函数
    public Response() {}
    // Getter和Setter方法

    // 状态码
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    // 响应信息提示
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // 响应数据
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
