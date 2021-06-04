package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Date 2021/6/4 18:29
 * @Author JINdc
 **/
@Controller
public class PassportController {

    @RequestMapping("login.html")
    public String login(HttpServletRequest request, String originUrl, Model model){
        String queryString = request.getQueryString();
        originUrl = queryString.replace("originUrl=", "");
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
