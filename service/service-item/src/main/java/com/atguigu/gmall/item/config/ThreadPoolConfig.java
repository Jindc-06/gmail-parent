package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2021/5/24 15:03
 * @Author JINdc
 **/
@Configuration
public class ThreadPoolConfig {

    //创建自定义线程池
    @Bean
    public ThreadPoolExecutor ThreadPool2Item(){
        return new ThreadPoolExecutor(30,60,2, TimeUnit.SECONDS,new ArrayBlockingQueue<>(5000));
    }
}
