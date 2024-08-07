package com.neu.monitor_sys.auth;

import com.neu.monitor_sys.auth.client.RoleClient;
import com.neu.monitor_sys.auth.client.UserClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {UserClient.class, RoleClient.class})
@MapperScan("com.neu.monitor_sys.auth.mapper")
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
