#### 记录使用rabbit  mq处理订单超时业务
>前言：这几天在收尾项目，发现对订单超时的业务做的不够细致，我们选择的是最为简单的定时任务来跑缓存中的订单，并筛选出超时的订单进行数据库更新操作，但是这样太消耗服务器的性能了，所以思考是否有别的解决方案

方案当然是有的，就我个人而言，就知道三种方案

 1. 就是我们现在所使用的方法，将订单存在redis缓存中，再模糊查询出来未付款的订单id列表，将id列表进行筛选，将筛选好的id列表进行数据库批量更新，
 2.  也是将订单id存在redis缓存中，不同的是，存入redis的同时设置过期时间，再开启redis过期回调功能，参考链接：[https://www.cnblogs.com/NJM-F/p/10442198.html](https://www.cnblogs.com/NJM-F/p/10442198.html)，**但是问题是**![在这里插入图片描述](https://img-blog.csdnimg.cn/20200401163357239.png)
3. 使用mq来消费，mq设置消息过期时间，消息过期后，将订单消息发送到队列中，程序监听该队列，进行数据库操作
- 综上所述，第三种应该是最适合我们的方法了，接下来就是具体操作了

---

##### 1.加入依赖
```java

<!--消息队列相关依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
<!--lombok依赖-->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

```

##### 2.修改application.yml配置文件

```bash
rabbitmq:
    host: localhost # rabbitmq的连接地址
    port: 5672 # rabbitmq的连接端口号
    virtual-host: /demo # rabbitmq的虚拟host
    username: root # rabbitmq的用户名
    password: root # rabbitmq的密码
    publisher-confirms: true #如果对异步消息需要回调必须设置为true
```
##### 3.添加消息队列的枚举配置类QueueEnum
    - 用于延迟消息队列及处理取消订单消息队列的常量定义，包括交换机名称、队列名称、路由键名称。 
```java

package com.xxx.xxx.xxx.enume;

import lombok.Getter;

/**
 * 消息队列枚举配置
 * @author demo
 */
@Getter
public enum QueueEnum {
    /**
     * 消息通知队列
     */
    QUEUE_ORDER_CANCEL("demo.order.direct", "demo.order.cancel", "demo.order.cancel"),
    /**
     * 消息通知ttl队列
     */
    QUEUE_TTL_ORDER_CANCEL("demo.order.direct.ttl", "demo.order.cancel.ttl", "demo.order.cancel.ttl");

    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
}

```

##### 4.增加RabbitMqConfig配置类
```java 

package com.xx.xx.xx.config;

import com.xx.xx.xx.xx.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置
 * @author demo
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange orderDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单延迟队列队列所绑定的交换机
     */
    @Bean
    DirectExchange orderTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单实际消费队列
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(QueueEnum.QUEUE_ORDER_CANCEL.getName());
    }

    /**
     * 订单延迟队列（死信队列）
     */
    @Bean
    public Queue orderTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_ORDER_CANCEL.getExchange())//到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey())//到期后转发的路由键
                .build();
    }

    /**
     * 将订单队列绑定到交换机
     */
    @Bean
    Binding orderBinding(DirectExchange orderDirect,Queue orderQueue){
        return BindingBuilder
                .bind(orderQueue)
                .to(orderDirect)
                .with(QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey());
    }

    /**
     * 将订单延迟队列绑定到交换机
     */
    @Bean
    Binding orderTtlBinding(DirectExchange orderTtlDirect,Queue orderTtlQueue){
        return BindingBuilder
                .bind(orderTtlQueue)
                .to(orderTtlDirect)
                .with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());
    }

}

```

###### 交换机及队列说明
- demo.order.direct（取消订单消息队列所绑定的交换机）:绑定的队列为demo.order.cancel，一旦有消息以demo.order.cancel为路由键发过来，会发送到此队列。
- demo.order.direct.ttl（订单延迟消息队列所绑定的交换机）:绑定的队列为demo.order.cancel.ttl，一旦有消息以demo.order.cancel.ttl为路由键发送过来，会转发到此队列，并在此队列保存一定时间，等到超时后会自动将消息发送到demo.order.cancel（取消订单消息消费队列）。

##### 5.添加延迟消息的发送者CancelOrderSender（用于向订单延迟消息队列（demo.order.cancel.ttl）里发送消息）

```java

package com.xx.xx.xx.component;


import com.xx.xx.xx.enume.QueueEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 取消订单消息的发出者
 * @author demo
 */
@Component
@Slf4j
public class CancelOrderSender {

    @Resource
    private AmqpTemplate amqpTemplate;

    public void sendMessage(String orderId,final long delayTimes){
        //给延迟队列发送消息
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange(),
                QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey(), orderId, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //给消息设置延迟毫秒值
                message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                return message;
            }
        });
        log.info("send delay message orderId:{}",orderId);
    }
}


```

##### 6.添加取消订单消息的接收者CancelOrderReceiver（用于从取消订单的消息队列（demo.order.cancel）里接收消息）
```java
package com.xx.xx.xx.component;


import com.xx.xx.main.service.OmsPortalOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 取消订单消息的处理者
 * @author demo
 */
@Slf4j
@Component
@RabbitListener(queues = "demo.order.cancel")
public class CancelOrderReceiver {

    @Resource
    private OmsPortalOrderService portalOrderService;

    @RabbitHandler
    public void handle(String orderId){

        log.info("receive delay message orderId:{}",orderId);
        portalOrderService.cancelOrder(orderId);
    }
}
```
##### 7.在具体业务逻辑中发送消息
```java

package com.xx.xx.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author abm
 */
@Slf4j
@Service
public class TransactionService implements OrderService{
	@Override
    public CommonResult generateOrder(OrderParam orderParam) {
        //todo 执行一系类下单操作
        LOGGER.info("process generateOrder");
        //下单完成后开启一个延迟消息，用于当用户没有付款时取消订单（orderId应该在下单后生成）
        sendDelayMessageCancelOrder(orderId);
        return CommonResult.success(null, "下单成功");
    }

    @Override
    public void cancelOrder(String orderId) {
        //todo 执行一系类取消订单操作
        LOGGER.info("process cancelOrder orderId:{}",orderId);
    }

    private void sendDelayMessageCancelOrder(String orderId) {
        //获取订单超时时间，假设为60分钟
        long delayTimes = 30 * 1000;
        //发送延迟消息
        cancelOrderSender.sendMessage(orderId, delayTimes);
    }

} 

```
---
#### 就这样，在超过15分钟之后，数据库的订单将自动修改为取消状态。但是还有一个问题在 mq 出问题的时候怎么处理超时订单呢？是搭一个mq集群还是写一个定时任务频率不需要太快呢?这个问题等待我下次来回答吧

##### [文章参考](http://www.macrozheng.com/#/architect/mall_arch_09?id=%E6%B7%BB%E5%8A%A0%E5%BB%B6%E8%BF%9F%E6%B6%88%E6%81%AF%E7%9A%84%E5%8F%91%E9%80%81%E8%80%85cancelordersender)