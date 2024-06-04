package com.neu.monitorSys.gateway;

import cn.hutool.core.util.StrUtil;
import com.neu.monitorSys.gateway.util.RedisUtil;
import com.nimbusds.jose.shaded.json.JSONObject;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.config.AuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
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

    @Autowired
    private AuthConfig myConfig;

    @Autowired
    private FeignAuthClient feignAuthClient;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpResponse response = exchange.getResponse();
        JSONObject message = new JSONObject();
        ServerHttpRequest request = exchange.getRequest();
        //1、获取请求路径
        String path = request.getPath().toString();

        //处理白名单内不需要进行拦截的地址
        if(!Objects.isNull(myConfig.getWhiteList())){
            for (String whitePath : myConfig.getWhiteList()) {
                if(path.contains(whitePath)){
                    return chain.filter(exchange);
                }
            }
        }

        //获取对应的token
        String token = "Token:"+request.getHeaders().getFirst("Authorization");
        //判断传递了token才可以继续解析
        if(!StrUtil.isEmpty(token)){
            try {
                //先去本地redis根据token获取value
                String userLogId = (String) redisUtil.get(token);
                //如果没有查到，或者过期时间小于10分钟，则去权限获取信息，权限会进行token续约
                if (Objects.isNull(userLogId) || redisUtil.getExpire(userLogId) < 600) {
                    String userId=feignAuthClient.validate(token).getData();
                    //如果校验token成功
                    if(null!=userId){
                        //缓存到redis中，一段时间内都不用校验token了
                        //token为key，用户id为value,存储7200秒（2小时）
                        redisUtil.set(token,userId,7200);
                        //放行继续向下调用
                        return chain.filter(exchange);
                    }
                }else{
                    return chain.filter(exchange);//放行继续向下调用
                }
            }catch (Exception e){
                //拦截请求，返回状态码401
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


    public class Config implements AuthConfig {
        @Override
        public String getMessageLayer() {
            return null;
        }

        @Override
        public String getAppContext() {
            return null;
        }

        @Override
        public String getAuthContextID(MessageInfo messageInfo) {
            return null;
        }

        @Override
        public void refresh() {

        }

        @Override
        public boolean isProtected() {
            return false;
        }
    }
}
