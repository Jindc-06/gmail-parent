package com.atguigu.gmall.payment.receiver;

import org.springframework.stereotype.Component;

/**
 * @Date 2021/6/7 21:20
 * @Author JINdc
 * 消息队列消费者
 **/
@Component
public class PaymentConsumer {
    //设置交换机,路由,队列
//    @RabbitListener(bindings = @QueueBinding(
//            exchange = @Exchange(value = "testExchange",durable = "true",autoDelete = "false"),
//            key = {"test.routing"},
//            value = @Queue
//    ))

}
