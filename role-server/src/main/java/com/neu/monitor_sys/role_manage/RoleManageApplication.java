package com.neu.monitor_sys.role_manage;

import com.neu.monitor_sys.role_manage.config.PermissionUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties(PermissionUrlConfig.class)
public class RoleManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoleManageApplication.class, args);
    }
}
