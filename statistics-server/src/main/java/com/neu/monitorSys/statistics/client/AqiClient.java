package com.neu.monitorSys.statistics.client;

import com.neu.monitorSys.common.DTO.AqiDTO;
import com.neu.monitorSys.common.DTO.MyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "aqi-server")
public interface AqiClient {
    @GetMapping("/api/v1/aqi/level")
    MyResponse getAqiLevel(@RequestParam("value") int value);

    @GetMapping("/api/v1/aqi/calculate")
    MyResponse<int[]> calculateAqi(@RequestParam("so2") int so2, @RequestParam("co") int co, @RequestParam("spm") int spm);
}
