package com.atguigu.gmall.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Date 2021/6/1 18:26
 * @Author JINdc
 **/
@FeignClient(value = "service-user")
public interface UserFeignClient {

}
