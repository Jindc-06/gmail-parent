package com.atguigu.gmall.search;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Date 2021/5/26 11:20
 * @Author JINdc
 **/
@FeignClient(value = "service-search")
public interface SearchFeignClient {

    //首页三级分类属性列表
    @RequestMapping("api/search/getCategoryToIndex")
    List<JSONObject> getCategoryToIndex();
}
