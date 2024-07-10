package com.neu.monitor_sys.gateway.fliter;

import cn.hutool.core.text.CharSequenceUtil;
import com.neu.monitor_sys.common.DTO.MyResponse;
import com.neu.monitor_sys.common.constants.ResultCode;
import com.neu.monitor_sys.common.constants.SecurityConstants;
import com.neu.monitor_sys.gateway.client.AuthClient;
import com.neu.monitor_sys.gateway.config.AuthConfig;
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

@Order(1)
@Component
@Slf4j
public class AuthorizationFilter implements GlobalFilter {

    @Resource
    @Lazy
    private AuthClient authClient;

    @Resource
    private AuthConfig myConfig;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        final ServerHttpResponse response = exchange.getResponse();
        final JSONObject message = new JSONObject();

        ServerHttpRequest request = exchange.getRequest();
        request = request.mutate().headers(httpHeaders -> httpHeaders.remove(SecurityConstants.FROM)).build();
        final String path = request.getURI().getPath();
        final String api = request.getMethod().name() + " " + path;

        if (myConfig.getWhiteList() != null) {
            for (String whitePath : myConfig.getWhiteList()) {
                if (api.contains(whitePath)) {
                    return chain.filter(exchange);
                }
            }
        }

        final String token = request.getHeaders().getFirst("Authorization");
        if (CharSequenceUtil.isEmpty(token)) {
            return unauthorizedResponse(response, message);
        }

        ServerHttpRequest finalRequest = request;
        return getAuthResult(token, api).flatMap(result -> {
            if (result.getStatusCode() == ResultCode.SUCCESS.getCode()) {
                final String logId = result.getData();
                ServerHttpRequest newRequest = finalRequest.mutate().header("logId", logId).build();
                ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
                return chain.filter(newExchange);
            } else {
                return unauthorizedResponse(response, message, result.getStatusCode(), result.getMessage());
            }
        }).onErrorResume(e -> {
            log.error("鉴权失败", e);
            return unauthorizedResponse(response, message);
        });
    }

    private Mono<MyResponse<String>> getAuthResult(String token, String api) {
        WebClient webClient = webClientBuilder.build();
        String url = "http://auth-server/api/v1/auth/validate?originURI=" + api;
        return webClient.get().uri(url).header("Authorization", token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MyResponse<String>>() {});
    }

    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, JSONObject message) {
        return unauthorizedResponse(response, message, HttpStatus.FORBIDDEN.value(), "访问超时");
    }

    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, JSONObject message, int statusCode, String errorMessage) {
        message.put("statusCode", statusCode);
        message.put("message", errorMessage);
        message.put("data", null);
        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }
}
