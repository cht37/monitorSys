package com.neu.monitorSys.user.controller;


import cn.hutool.core.util.ObjectUtil;
import com.neu.monitorSys.user.DTO.MemberWithRole;
import com.neu.monitorSys.user.DTO.MyResponse;
import com.neu.monitorSys.user.constants.ResultCode;
import com.neu.monitorSys.user.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表	 前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
@RestController
@RequestMapping("//member")
public class MemberController {
    @Autowired
    private IMemberService memberService;
    /**
     * 获取用户基本信息
     */
    @GetMapping("/getMemberInfo/{logId}")
    public MyResponse<MemberWithRole> getMemberInfo(@PathVariable String logId) {
        MemberWithRole memberWithRole = memberService.getMemberWithRole(logId);
        if(ObjectUtil.isNull(memberWithRole)){
            return new MyResponse<>(ResultCode.FAILED.getCode(), "获取用户信息失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "获取用户信息成功", memberWithRole);
    }
}

