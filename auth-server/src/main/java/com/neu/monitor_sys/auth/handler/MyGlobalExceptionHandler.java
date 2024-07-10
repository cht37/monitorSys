package com.neu.monitor_sys.auth.handler;

import com.neu.monitor_sys.common.DTO.MyResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyGlobalExceptionHandler {
 // 处理所有未捕获的异常
    @ExceptionHandler({ AuthenticationException.class })
    @ResponseBody
    public ResponseEntity<MyResponse<String>> handleAuthenticationException(Exception ex) {

        MyResponse<String> re = new MyResponse<>(401, "error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
    }
    @ExceptionHandler({ RuntimeException.class })
    @ResponseBody
    public MyResponse<String> handleRuntimeException(Exception ex) {

        MyResponse<String> re = new MyResponse<>(403, "错误", ex.getMessage());
        return re;
    }
//    // 处理特定类型的异常，例如IllegalArgumentException
//    @ExceptionHandler(IllegalArgumentException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
//        // 返回错误信息
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//    }

    // 可以添加更多的@ExceptionHandler方法来处理其他类型的异常

}
