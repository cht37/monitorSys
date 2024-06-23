package com.neu.monitorSys.auth.controller;

import com.neu.monitorSys.auth.service.SysUserService;
import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.auth.constants.ResultCode;
import com.neu.monitorSys.entity.DTO.SysUserDTO;
import com.neu.monitorSys.entity.Member;
import com.neu.monitorSys.auth.service.impl.JwtService;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.Resource;
import org.apache.ibatis.jdbc.Null;
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
                return new MyResponse<>(ResultCode.UNAUTHORIZED.getCode(), "令牌过期，请重新登录",null);
            }
        } catch (ParseException e) {
            return new MyResponse<>(ResultCode.BAD_REQUEST.getCode(), e.getMessage(), null);
        } catch (JOSEException e) {
            return new MyResponse<>(ResultCode.BAD_REQUEST.getCode(), e.getMessage(), null);
        } catch (AuthenticationException e) {
            return new MyResponse<>(ResultCode.UNAUTHORIZED.getCode(), e.getMessage(), null);
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

}
