package com.neu.monitorSys.user.controller;


import cn.hutool.core.util.ObjectUtil;
//import com.neu.monitorSys.authority.IgnoreToken;
//import com.neu.monitorSys.authority.IgnoreToken;
import com.neu.monitorSys.entity.DTO.MemberWithRole;
import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.user.constants.ResultCode;
import com.neu.monitorSys.entity.Member;
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
@RequestMapping("/api/v1/members")
public class MemberController {
    @Autowired
    private IMemberService memberService;

    /**
     * 根据logId获取用户信息
     * @param logId
     * @return
     */
    @GetMapping("/info")
    public MyResponse<MemberWithRole> getMemberInfo(@RequestHeader("logId") String logId) {
        MemberWithRole memberWithRole = memberService.getMemberWithRole(logId);
        if(ObjectUtil.isNull(memberWithRole)){
            return new MyResponse<>(ResultCode.FAILED.getCode(), "获取用户信息失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "获取用户信息成功", memberWithRole);
    }

    /**
     * 修改用户信息
     * @param member
     * @return MyResponse<Boolean>
     */
   @PutMapping
    public MyResponse<Boolean> updateMemberInfo(@RequestBody Member member) {
        boolean update = memberService.updateMember(member);
        if(!update){
            return new MyResponse<>(ResultCode.FAILED.getCode(), "修改用户信息失败", false);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "修改用户信息成功", true);
    }
    /**
     * 获取用户基本信息
     * @param logId
     * @param
     * @return
     */
    @GetMapping("/basic/{logId}")
//    @IgnoreToken(value = false)
    public MyResponse<Member> getMember(@PathVariable String logId) {
        Member member = memberService.getMember(logId);
        if(ObjectUtil.isNull(member)){
            return new MyResponse<>(ResultCode.FAILED.getCode(), "获取用户信息失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "获取用户信息成功", member);
    }
    /**
     * 根据手机号获取用户基本信息
     * @param mobile
     * @return
     */
    @GetMapping("/by-mobile/{mobile}")
    public MyResponse<Member> getMemberByMobile(@PathVariable String mobile) {
        Member member = memberService.getMemberByMobile(mobile);
        if(ObjectUtil.isNull(member)){
            return new MyResponse<>(ResultCode.FAILED.getCode(), "获取用户信息失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "获取用户信息成功", member);
    }

    /**
     * 根据id获取用户姓名
     * @param logId
     * @return
     */
    @GetMapping("/name")
    public MyResponse<String> getName(@RequestParam String logId) {
        String name = memberService.getNameById(logId);
        if(name == null){
            return new MyResponse<>(ResultCode.FAILED.getCode(), "获取用户姓名失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "获取用户姓名成功", name);
    }
    /**
     * 根据id获取用户角色id
     * @param logId
     * @return
     */
    @GetMapping("/role-id/{logId}")
    public MyResponse<Integer> getRoleIdByLogId(@PathVariable String logId) {
        Integer id = memberService.getRoleIdByLogId(logId);
        if(id == null){
            return new MyResponse<>(ResultCode.FAILED.getCode(), "获取用户id失败", null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "获取用户id成功", id);
    }

    /**
     * 新增用户
     * @param member
     * @return
     */
    @PostMapping("/add")
    public MyResponse<Boolean> addMember(@RequestBody Member member) {
        try {
            memberService.saveMember(member);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "新增用户失败"+e.getMessage(), false);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "新增用户成功", true);
    }
}

