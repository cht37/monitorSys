package com.neu.monitorSys.user.controller;


import cn.hutool.core.util.ObjectUtil;
import com.neu.monitorSys.user.DTO.MemberWithRole;
import com.neu.monitorSys.user.DTO.MyResponse;
import com.neu.monitorSys.user.constants.ResultCode;
import com.neu.monitorSys.user.entity.Member;
import com.neu.monitorSys.user.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表	 前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private IMemberService memberService;

    /**
     * 根据logId获取用户信息
     * @param logId
     * @return
     */
    @GetMapping("/getMemberInfo/{logId}")
    public MyResponse<MemberWithRole> getMemberInfo(@PathVariable String logId) {
        MemberWithRole memberWithRole = memberService.getMemberWithRole(logId);
        if(ObjectUtil.isNull(memberWithRole)){
            return new MyResponse<>(ResultCode.FAILED.getCode(), "获取用户信息失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "获取用户信息成功", memberWithRole);
    }

    /**
     * 修改用户信息
     * @param member
     * @return
     */
    @GetMapping("/updateMemberInfo")
    public MyResponse<Boolean> updateMemberInfo(@RequestBody Member member) {
        boolean update = memberService.updateMember(member);
        if(!update){
            return new MyResponse<>(ResultCode.FAILED.getCode(), "修改用户信息失败", false);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "修改用户信息成功", true);
    }



}

