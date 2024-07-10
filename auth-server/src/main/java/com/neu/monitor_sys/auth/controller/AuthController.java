package com.neu.monitor_sys.auth.controller;

import com.neu.monitor_sys.auth.DTO.ModifyPasswordDTO;
import com.neu.monitor_sys.auth.service.SysUserService;
import com.neu.monitor_sys.common.DTO.MyResponse;
import com.neu.monitor_sys.auth.constants.ResultCode;
import com.neu.monitor_sys.common.DTO.SysUserDTO;
import com.neu.monitor_sys.auth.service.impl.JwtService;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.text.ParseException;

@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthController {

    @Autowired
    private SysUserService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Resource
    private JwtService jwtService;


//    @PostMapping(value = "/register")
//    public MyResponse<Null> createUser(@RequestBody Member member) {
//        member.setLogpwd(passwordEncoder.encode(member.getLogpwd()));
//        authService.saveMember(member);
//        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", null);
//    }

    /**
     * 校验token有效性
     *
     * @param token
     * @return
     * @throws ParseException
     * @throws JOSEException
     */
    @GetMapping(value = "/validate")
    public MyResponse<String> validateToken(@RequestHeader("Authorization") String token,@RequestParam String originURI) throws ParseException, JOSEException, AuthenticationException {
        try {
            String logId = jwtService.validateToken(token);
            if(logId!=null&&!logId.equals("")){
                return new MyResponse<>(ResultCode.SUCCESS.getCode(),"允许访问",logId);
            }else{
                return new MyResponse<>(ResultCode.UNAUTHORIZED.getCode(), "无权限访问",null);
            }
        } catch (ParseException | JOSEException e) {
            return new MyResponse<>(ResultCode.BAD_REQUEST.getCode(), e.getMessage(), null);
        } catch (AuthenticationException e) {
            return new MyResponse<>(ResultCode.LOGIN_EXPIRED.getCode(), e.getMessage(), null);
        }
    }

    /**
     * 注册
     * @param sysUserDTO 用户密码信息
     * @return
     */
    @PostMapping(value = "/public/register")
    public MyResponse<String> register(@RequestBody SysUserDTO sysUserDTO) {
        try {
            boolean saved = authService.savePublicUser(sysUserDTO);
            if (saved) {
                return new MyResponse<>(ResultCode.SUCCESS.getCode(), "注册成功", null);
            } else {
                return new MyResponse<>(ResultCode.SERVER_ERROR.getCode(), "注册失败", null);
            }
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.BAD_REQUEST.getCode(), e.getMessage(), null);
        }

    }

    /**
     * 修改密码
     * @param modifyPasswordDTO 修改密码信息
     * @param logId 用户id
     * @return 是否修改成功
     */
    @PostMapping(value = "/password/change")
    public MyResponse<Boolean> modifyPassword(@RequestBody ModifyPasswordDTO modifyPasswordDTO, @RequestHeader("logId") String logId){
        try {
            boolean result = authService.modifyPassword(modifyPasswordDTO, logId);
            if (result) {
                return new MyResponse<>(ResultCode.SUCCESS.getCode(), "修改密码成功", true);
            } else {
                return new MyResponse<>(ResultCode.SERVER_ERROR.getCode(), "修改密码失败", false);
            }
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.BAD_REQUEST.getCode(), "修改密码失败"+e.getMessage(), false);
        }
    }

    /**
     * 注册普通用户
     * @param sysUserDTO 用户信息
     * @return 是否保存成功
     */
    @PostMapping(value = "/normal/register")
    public MyResponse<Boolean> saveNormalUser(@RequestBody SysUserDTO sysUserDTO) {
        try {
            boolean result = authService.saveNormalUser(sysUserDTO);
            if (result) {
                return new MyResponse<>(ResultCode.SUCCESS.getCode(), "注册成功", true);
            } else {
                return new MyResponse<>(ResultCode.SERVER_ERROR.getCode(), "注册失败", false);
            }
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.BAD_REQUEST.getCode(), "注册失败"+e.getMessage(), false);
        }
    }

    /**
     * 重置用户密码
     * @param logId 用户id
     * @return 是否成功
     */
    @PostMapping(value = "/pwd/reset")
    public MyResponse<Boolean> reversePassword(@RequestBody String logId){
        boolean result;
        try {
             result=authService.reversePassword(logId);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "重置密码失败"+e.getMessage(),false);
        }
        if(result){
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "重置密码成功",true);
        }else {
             return new MyResponse<>(ResultCode.FAILED.getCode(), "重置密码失败",false);
        }
    }
}
