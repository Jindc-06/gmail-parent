package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.search.SearchFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Date 2021/5/26 11:14
 * @Author JINdc
 **/
@Controller
public class SearchController {

    @Autowired
    private SearchFeignClient searchFeignClient;

    //首页三级分类属性列表
    @RequestMapping({"index.html","/"})
    public String index(Model model){
        List<JSONObject> jsonObjects = searchFeignClient.getCategoryToIndex();
        model.addAttribute("list",jsonObjects);
        return "index/index";
    }
}
