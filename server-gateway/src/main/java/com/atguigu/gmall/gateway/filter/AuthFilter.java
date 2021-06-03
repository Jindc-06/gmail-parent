package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.client.UserFeignClient;
import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.common.util.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @Date 2021/6/1 18:21
 * @Author JINdc
 **/
@Component
public class AuthFilter implements GlobalFilter {

    @Autowired
    UserFeignClient userFeignClient;

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${authUrls.url}")
    String authUrlS;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        URI uri = request.getURI();
        String uriString = uri.toString();
        String path = uri.getPath();
        //静态资源请求和登录
        if (uri.equals(".jpg")||uri.equals(".png")||uri.equals(".ico")||uri.equals(".js")||uri.equals(".css")||uri.equals("passport")){
            return chain.filter(exchange);
        }
        //内部请求(静止访问)
        if (antPathMatcher.match("/**/inner/**",path)){
            return out(response,ResultCodeEnum.SECKILL_ILLEGAL);
        }
        //web请求
        String[] split = authUrlS.split(",");
        for (String authUrl : split) {
            if (uriString.contains(authUrl)){
                //如果请求在白名单中,则需要进行身份验证
                Map<String,Object> verifyMap = userFeignClient.verify(getCookieOrHeaderValue(request,"token"));
                String success = (String) verifyMap.get("success");
                String userId = (String) verifyMap.get("userId");
                if (!StringUtils.isEmpty(success)&&success.equals("success")){
                    //验证成功放行
                    chain.filter(exchange);
                }else {
                    //设置返回给请求的错误代号
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    //重定向到登录页面
                    response.getHeaders().set(HttpHeaders.LOCATION,"http://passport.gmall.com/login.html?originUrl="+request.getURI());
                    //完成设置 , 返回信息
                    Mono<Void> voidMono = response.setComplete();
                    return voidMono;
                }
            }
        }
        return chain.filter(exchange);
    }
    //从cookie获取token
    private String getCookieOrHeaderValue(ServerHttpRequest request,String token){
        String tokenResult = "";
        MultiValueMap<String, HttpCookie> cookieMap = request.getCookies();
        if (cookieMap!=null && cookieMap.size()>0){
            List<HttpCookie> cookieList = cookieMap.get(token);
            if (cookieList!=null && cookieList.size()>0){
                for (HttpCookie cookie : cookieList) {
                    tokenResult = cookie.getValue();
                }
            }
        }
        return tokenResult;
    }


    //返回一个json字符串(提示不可访问)
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum){
        Result<Object> result = Result.build(null, resultCodeEnum);
        byte[] bytes = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        //设置编码格式
        response.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        Mono<Void> voidMono = response.writeWith(Mono.just(wrap));
        return voidMono;
    }
}
