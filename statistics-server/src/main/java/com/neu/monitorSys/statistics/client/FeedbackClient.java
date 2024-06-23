package com.neu.monitorSys.statistics.client;

import com.neu.monitorSys.entity.AqiFeedback;
import com.neu.monitorSys.entity.DTO.MyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("feedback-server")
public interface FeedbackClient {
    @GetMapping("/api/v1/feedbacks/{id}")
    MyResponse findFeedbackById(@PathVariable("id") Integer id);
    @GetMapping("/api/v1/feedbacks/{afId}/state")
    MyResponse<Boolean> updateFeedbackState(@PathVariable("afId") Integer afId, @RequestParam("state") Integer state);
}
