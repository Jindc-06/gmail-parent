package com.atguigu.gmall.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Date 2021/6/4 15:54
 * @Author JINdc
 **/
public class FeignInterceptor implements RequestInterceptor {

    /**
     * feign拦截器
     * 网关.将老的request中的userId传递到新的request中
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        requestTemplate.header("userTempId", request.getHeader("userTempId"));
        requestTemplate.header("userId", request.getHeader("userId"));
    }
}
