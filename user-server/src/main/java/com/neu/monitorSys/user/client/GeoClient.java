package com.neu.monitorSys.user.client;

import com.neu.monitorSys.entity.DTO.MyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geography-server")
public interface GeoClient {
    @GetMapping("/api/v1/areas/{id}")
    MyResponse getGeoInfo(@PathVariable("id") Integer id);

    @GetMapping("/api/v1/areas/grid-id")
    MyResponse getGridIdByGridName(@RequestParam("gridName") String gridName);
}
