package com.atguigu.gmall.client;

import com.atguigu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * @Date 2021/6/1 18:26
 * @Author JINdc
 **/
@FeignClient(value = "service-user")
public interface UserFeignClient {

    //身份验证
    @RequestMapping("api/user/passport/verify/{token}")
    Map<String, Object> verify(@PathVariable("token") String token);
    //用户地址
    @RequestMapping("api/user/passport/getUserAddresses")
    List<UserAddress> getUserAddresses();
}
