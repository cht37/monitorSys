package com.neu.monitorSys.user.client;

import com.neu.monitorSys.user.DTO.MyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "geography-server")
public interface GeoClient {
    @GetMapping("/area/getGeoInfo/{id}")
    MyResponse getGeoInfo(@PathVariable("id") Integer id);
}
