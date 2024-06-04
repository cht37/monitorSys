package com.neu.monitorSys.gateway;

import cn.hutool.core.util.StrUtil;
import com.neu.monitorSys.gateway.config.AuthConfig;
import com.neu.monitorSys.gateway.constants.ResultCode;
import com.neu.monitorSys.gateway.util.RedisUtil;
import com.nimbusds.jose.shaded.json.JSONObject;
import jakarta.annotation.Resource;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Order(1)
@Component
public class AuthorizationFilter implements GlobalFilter {

    //    @Resource
//    private AuthConfig myConfig;
    private final FeignAuthClient feignAuthClient;

    @Autowired
    public AuthorizationFilter(@Lazy FeignAuthClient feignAuthClient) {
        this.feignAuthClient = feignAuthClient;
    }

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private AuthConfig myConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpResponse response = exchange.getResponse();
        JSONObject message = new JSONObject();
        ServerHttpRequest request = exchange.getRequest();
        //1、获取请求路径
        String path = request.getPath().toString();

        //处理白名单内不需要进行拦截的地址
        if (!Objects.isNull(myConfig.getWhiteList())) {
            for (String whitePath : myConfig.getWhiteList()) {
                if (path.contains(whitePath)) {
                    return chain.filter(exchange);
                }
            }
        }
        //Todo 有问题
        //获取对应的token
        String token = request.getHeaders().getFirst("Authorization");
        //判断传递了token才可以继续解析
        if (!StrUtil.isEmpty(token)) {
            //调用auth-server的验证接口
            val result = feignAuthClient.validate(token);
            if (result.getStatusCode() == ResultCode.SUCCESS.getCode()) {
                return chain.filter(exchange);
            } else {
                message.put("status", -1);
                message.put("data", "鉴权失败");
                byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bits);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                //指定编码，否则在浏览器中会中文乱码
                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                return response.writeWith(Mono.just(buffer));
            }
        }
        message.put("status", -1);
        message.put("data", "鉴权失败");
        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }
}

