package com.neu.monitorSys.gateway.fliter;

import cn.hutool.core.util.StrUtil;
import com.neu.monitorSys.common.DTO.MyResponse;
import com.neu.monitorSys.common.constants.ResultCode;
import com.neu.monitorSys.common.constants.SecurityConstants;
import com.neu.monitorSys.gateway.client.AuthClient;
import com.neu.monitorSys.gateway.config.AuthConfig;
import com.nimbusds.jose.shaded.json.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.*;

@Order(1)
@Component
@Slf4j
public class AuthorizationFilter implements GlobalFilter {

    //    @Resource
//    private AuthConfig myConfig;
    @Resource
    @Lazy
    private AuthClient authClient;

    private final ExecutorService executorService;


    @Resource
    private AuthConfig myConfig;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public AuthorizationFilter() {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpResponse response = exchange.getResponse();
        JSONObject message = new JSONObject();

        ServerHttpRequest request = exchange.getRequest();
        // 清洗请求头中from 参数
        request = exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove(SecurityConstants.FROM)).build();
        //1、获取请求路径
        String path = request.getURI().getPath();
        String api = request.getMethod().name() + " " + path;


        //处理白名单内不需要进行拦截的地址
        if (!Objects.isNull(myConfig.getWhiteList())) {
            for (String whitePath : myConfig.getWhiteList()) {
                if (api.contains(whitePath)) {
                    return chain.filter(exchange);
                }
            }
        }

        //获取对应的token
        String token = request.getHeaders().getFirst("Authorization");
        //判断传递了token才可以继续解析
        if (!StrUtil.isEmpty(token)) {
            //调用auth-server的验证接口
            //获取账号
//            Response<String> result = feignAuthClient.validate(token, path);

//            //阻塞同步获取返回结果
//            Response<String> result = null;
//            try {
//                result = f.get();
//            } catch (InterruptedException | ExecutionException e) {
//                throw new RuntimeException(e);
//            }4
            MyResponse<String> result = getAuthResult(token, api);

            if (result.getStatusCode() == ResultCode.SUCCESS.getCode()) {
                String logId = result.getData();
                //将logId存入请求头
                ServerHttpRequest newRequest = request.mutate().header("logId", logId).build();
                exchange = exchange.mutate().request(newRequest).build();
                return chain.filter(exchange);
            } else {
                message.put("statusCode", result.getStatusCode());
                message.put("message", "鉴权失败");
                message.put("data", null);
                byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bits);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                //指定编码，否则在浏览器中会中文乱码
                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                return response.writeWith(Mono.just(buffer));
            }
        }
        message.put("status", 401);
        message.put("message", "鉴权失败");
        message.put("data", null);
        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    //    private MyResponse<String> getAuthResult(String token, String api) {
//        CompletableFuture<MyResponse<String>> future = CompletableFuture.supplyAsync(
//                // 在异步包装内执行我们的微服务请求
//                () -> authClient.validate(token, api)
//        );
//        try {
//            // 等待相应返回结果
//            return future.get();
//        } catch (Exception ex) {
//            String msg = "网关检查接口是否存在,发生异常";
//            log.error(msg, ex);
//            throw new RuntimeException(msg);
//        }
//    }
    //线程池
//            ExecutorService executorService = Executors.newCachedThreadPool();
//            Mono<MyResponse> responseMono = webClient.get()
//                    .uri("/api/v1/auth/validate?"+"originURI=" + api)
//                    .header("Authorization", token)
//                    .retrieve()
//                    .bodyToMono(MyResponse.class);
//            MyResponse result = responseMono.block(Duration.ofSeconds(1));
//            return result;
//
    private MyResponse<String> getAuthResult(String token, String api) {
        WebClient webClient = webClientBuilder.build();
        String url = "http://auth-server/api/v1/auth/validate?originURI=" + api;
        Mono<MyResponse<String>> mono = webClient.get().uri(url).header("Authorization", token).retrieve().bodyToMono(new ParameterizedTypeReference<MyResponse<String>>() {
        });
        Future future = executorService.submit((Callable<MyResponse<String>>) () -> mono.block());
        try {
            return (MyResponse<String>) future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("网关检查接口是否存在,发生异常", e);
            throw new RuntimeException("网关检查接口是否存在,发生异常");
        } catch (TimeoutException e) {
            throw new RuntimeException("网关检查接口是否存在,超时异常");
        }
    }
}

