package com.neu.monitorSys.user.client;

import com.neu.monitorSys.entity.AqiFeedback;
import com.neu.monitorSys.entity.DTO.MyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "feedback-server")
public interface FeedbackClient {
        @GetMapping("/api/v1/feedbacks/{id}")
       MyResponse findFeedbackById(@PathVariable("id") Integer id);

}
