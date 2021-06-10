package com.atguigu.gmall.payment.receiver;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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


    @RabbitListener(queues = "queue.delay.1")
    public void a(Message message, Channel channel,String messageStr){

    }

}
