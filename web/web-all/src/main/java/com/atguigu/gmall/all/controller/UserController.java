package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Date 2021/6/2 21:01
 * @Author JINdc
 **/
@Controller
public class UserController {

    //登录
    @RequestMapping("login.html")
    public String login(Model model, HttpServletRequest request){
        String queryString = request.getQueryString();
        String originUrl = queryString.replace("originUrl=", "");
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
