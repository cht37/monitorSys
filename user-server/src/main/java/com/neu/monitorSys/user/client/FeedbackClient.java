package com.neu.monitorSys.user.client;

import com.neu.monitorSys.common.DTO.MyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "feedback-server")
public interface FeedbackClient {
        @GetMapping("/api/v1/feedbacks/{id}")
       MyResponse findFeedbackById(@PathVariable("id") Integer id);

        @PatchMapping("/api/v1/feedbacks/{afId}/state")
        MyResponse<Boolean> updateFeedbackState(@PathVariable("afId") Integer afId,@RequestParam("state") Integer state);
}
