### 记录使用redis过期回调解决订单超时问题

>此文章主要是之前一篇文章的拓展，上一篇文章说到，订单超时有三种处理方法，这篇文章记录的就是第二种解决方法

##### 上一篇文章地址
[记录使用rabbit mq处理订单超时业务](https://blog.csdn.net/weixin_43864927/article/details/105248902)

下面开始实际操作
---

1. 修改redis相关事件配置，添加 notify-keyspace-events Ex
    1. 直接修改redis.conf文件
    2. 或者使用RedisDesktopManager远程连接工具open console按钮，使用命令行`CONFIG SET notify-keyspace-events Ex`解决
    **但是两种方法设置完后都必须要重启redis服务，否则不会生效**
    
2. 加入maven依赖

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>


<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>RELEASE</version>
    <scope>compile</scope>
</dependency>


<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <scope>compile</scope>
    <version>2.9.7</version>
</dependency>
```
3.  定义配置RedisListenerConfig

```java
@Configuration
public class RedisListenerConfig {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 处理乱码
     * @return
     */
    @Bean
    public RedisTemplate redisTemplateInit() {
        // key序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //val实例化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
```
4. 定义监听器，实现**KeyExpirationEventMessageListener**接口

```java
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 针对redis数据失效事件，进行数据处理
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 用户做自己的业务处理即可,注意message.toString()可以获取失效的key
        String expiredKey = message.toString();
        if(expiredKey.startsWith("test:")){
            //如果是test:开头的key，进行处理
            System.out.println(expiredKey);
            String substring = expiredKey.split(":")[1]; //去掉orderNo
            System.out.println("------!!!!" + substring + "!!!-----");
        }
    }
}

```

5. 测试是否起作用

```java
public class Test(){
　　　　@Resource
　　　　private RedisTemplate redisTemplate;
　　　　redisTemplate.opsForValue().set("test:123","dadada" ,25, TimeUnit.SECONDS );
}
```
打开debug调试，发现30s后，能进行回调![在这里插入图片描述](https://img-blog.csdnimg.cn/20200402093454420.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg2NDkyNw==,size_16,color_FFFFFF,t_70)
输出结果如下，证明回调成功，该方法真实可行

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200402093619159.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg2NDkyNw==,size_16,color_FFFFFF,t_70)

[文章参考](https://www.cnblogs.com/NJM-F/p/10442198.html)

#### 为什么不适用该种方法的原因，在上一篇文章里解释过了，所以就不再解释一遍了，写这篇文章的目的是为了证明该方法也是切实可行的，只是因为不适合所以没有选择作为我们项目的解决方法，感兴趣的小伙伴去看我上一篇文章吧，文章链接在本文一开始就有哦