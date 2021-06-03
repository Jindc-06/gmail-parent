package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Date 2021/6/1 18:33
 * @Author JINdc
 **/
@RestController
@RequestMapping("api/user/passport")
public class UserApiController {

    @Autowired
    private UserService userService;

    //身份验证
    @RequestMapping("verify/{token}")
    Map<String, Object> verify(@PathVariable("token") String token){
        Map<String, Object> verifyMap = userService.verify(token);
        return verifyMap;
    }
    //登录
    @RequestMapping("login")
    Result login(@RequestBody UserInfo userInfo){
        UserInfo userInfoResult = userService.login(userInfo);
        if (userInfoResult != null){
            return Result.ok(userInfoResult);
        }else {
            return Result.fail("登录失败");
        }
    }
}
