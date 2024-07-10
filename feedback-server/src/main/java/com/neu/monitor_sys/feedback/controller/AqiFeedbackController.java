package com.neu.monitor_sys.feedback.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitor_sys.common.entity.AqiFeedback;
import com.neu.monitor_sys.common.DTO.MyResponse;
import com.neu.monitor_sys.common.constants.ResultCode;
import com.neu.monitor_sys.feedback.DTO.AqiFeedBackVO;
import com.neu.monitor_sys.feedback.DTO.AqiFeedbackDTO;
import com.neu.monitor_sys.feedback.DTO.AssignDTO;
import com.neu.monitor_sys.feedback.DTO.FeedbackQueryDTO;
import com.neu.monitor_sys.feedback.service.IAqiFeedbackService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 公众监督反馈表 前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-11
 */
@RestController
@Log4j2
@RequestMapping("/api/v1/feedbacks")
public class AqiFeedbackController {
    @Autowired
    private IAqiFeedbackService aqiFeedbackService;

    /**
     * 存储反馈信息
     * @param aqiFeedbackVO
     */
    /*
    @PostMapping("/saveFeedback")
    public void saveFeedback(@RequestBody AqiFeedbackVO aqiFeedbackVO) {
        Boolean aBoolean = aqiFeedbackService.saveFeedback(aqiFeedbackVO);
        if (aBoolean) {
            log.info("反馈信息存储成功");
        } else {
            log.info("反馈信息存储失败");
        }
    }
     */

    /**
     * 根据公众监督员电话号码查询反馈信息
     * @param telId 公众监督员电话号码
     * @param page 页数
     * @param size 每页大小
     * @return IPage<AqiFeedBackVO> 反馈信息
     */
    @GetMapping("/tel/{telId}")
    public MyResponse<IPage<AqiFeedBackVO>> getFeedbackByTelId(@PathVariable String telId,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        IPage<AqiFeedBackVO> feedbacks = null;
        try {
            feedbacks = aqiFeedbackService.getFeedbackByTelId(telId, page, size);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败", null);
        }

        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", feedbacks);
    }

    /**
     * 接收并提交反馈信息
     * @param feedback
     * @return
     */
    @PostMapping
    public MyResponse<Boolean> receiveFeedBack(@RequestBody AqiFeedbackDTO feedback){
        Boolean aBoolean = null;
        try {
            aBoolean = aqiFeedbackService.submitFeedback(feedback);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "提交失败"+e.getMessage(), false);
        }
        if (aBoolean) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "提交成功", true);
        } else {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "提交失败", false);
        }
    }

    /**
     * 指派网格员
     * @param assignDTO
     * @return
     */
    @PostMapping("/assign")
    public MyResponse<Boolean> assign(@RequestBody AssignDTO assignDTO){
        Boolean aBoolean = null;
        try {
            aBoolean = aqiFeedbackService.setFeedbackGridMember(assignDTO);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "指派失败"+e.getMessage(), false);
        }
        if (aBoolean) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "指派成功", true);
        } else {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "指派失败", false);
        }

    }

    /**
     * 根据反馈id查询反馈信息
     * @param id
     * @return
     */
     @GetMapping("/{id}")
    public MyResponse<AqiFeedback> findPageBackById(@PathVariable Integer id){
        AqiFeedback feedback = null;
        try {
            feedback = aqiFeedbackService.getFeedbackById(id);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", feedback);
    }

    /**
     * 修改反馈状态
     * @param afId
     * @param state
     * @return
     */
   @PatchMapping("/{afId}/state")
    public MyResponse<Boolean> updateFeedbackState(@PathVariable Integer afId,@RequestParam Integer state){
        Boolean aBoolean = null;
        try {
            aBoolean = aqiFeedbackService.updateFeedbackState(afId,state);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "修改失败"+e.getMessage(), false);
        }
        if (aBoolean) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "修改成功", true);
        } else {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "修改失败", false);
        }
    }

    /**
     * 根据条件查询反馈信息
     * @param feedbackQueryDTO 查询条件
     * @param page 页号
     * @param size 大小
     * @return  返回查询结果
     */
    @GetMapping("/search")
    public MyResponse<IPage<AqiFeedBackVO>> getFeedBackByConditions(@ModelAttribute FeedbackQueryDTO feedbackQueryDTO,
                                                                   @RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size){
        IPage<AqiFeedBackVO> feedbacks = null;
        try {
            feedbacks = aqiFeedbackService.getFeedBackByConditions(feedbackQueryDTO, page, size);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败"+e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", feedbacks);
    }

    /**
     * 根据网格id查询反馈信息（待处理的信息以及正在处理的信息）
     * @param gridId 网格id
     * @param page 页数
     * @param size 每页大小
     * @return IPage<AqiFeedBackVO>
     */
    @GetMapping("/waiting-list")
    public MyResponse<IPage<AqiFeedBackVO>> getFeedbackByGridId(@RequestHeader("logId") String gridId,
                                                                @RequestParam(required = false) Integer finish,
                                                                @RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        IPage<AqiFeedBackVO> feedbacks = null;
        try {
            feedbacks = aqiFeedbackService.getFeedbackByGridId(gridId,finish, page, size);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败"+e.getMessage(), null);
        }

        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", feedbacks);
    }


    /**
     * 获取当前处理的反馈指派
     * @param logId 网格员id
     * @return AqiFeedBackVO
     */
    @GetMapping("/current")
    public MyResponse<AqiFeedBackVO> getCurrentFeedback(@RequestHeader("logId") String logId){
        AqiFeedBackVO feedback = null;
        try {
            feedback = aqiFeedbackService.getCurrentFeedback(logId);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(),"查询失败"+e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", feedback);
    }
}

