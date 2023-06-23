package com.Config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.constant.UserMomentsConstant;
import com.domin.UserFollowing;
import com.domin.UserMoment;
import com.mysql.cj.util.StringUtils;
import com.service.UserFollowingService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;

    //TODO   绑定生产者
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws MQClientException {
        //TODO 实例化一个生产者 后面是name
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);
        //TODO 设置 代理的ip地址
        producer.setNamesrvAddr(nameServerAddr);
        //TODO 启动
        producer.start();
        return producer;
    }

    //TODO 绑定消费者
    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws MQClientException {
        //TODO 实例化一个消费者  后面是名字
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        //TODO 设置 代理的ip地址
        consumer.setNamesrvAddr(nameServerAddr);

        //TODO 订阅主题为 TOPIC 的消息  使用subExpression过滤信息  *表示全接收
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS, "*");

        //TODO  给消费者绑定监听 代理中间件
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt msg = list.get(0);
                if (msg != null) {
                    //TODO 拿出消息体
                    String body = new String(msg.getBody());
                    UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(body), UserMoment.class);
                    //TODO 查看用户ID  (哪个用户发送了消息)
                    Long userId = userMoment.getUserId();
                    //TODO 获取该用户的粉丝
                    List<UserFollowing> userFans = userFollowingService.getUserFans(userId);

                    //TODO 遍历每个粉丝
                    for (UserFollowing fan : userFans) {
                        //TODO 消息发布会放到redis里 然后从redis取出来
                        //TODO 因为直接存放会替换掉里面的数据 所以需要先拿出来 再将数据追加
                        String key = "subscribed-" + fan.getUserId();
                        String s = redisTemplate.opsForValue().get(key);
                        List<UserMoment> lists;
                        if (StringUtils.isNullOrEmpty(s)) {
                            lists = new ArrayList<>();
                        } else {
                            lists = JSONArray.parseArray(s, UserMoment.class);
                        }
                        lists.add(userMoment);
                        //TODO 最后把数据存回 Redis
                        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(lists));
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        //TODO 启动
        consumer.start();
        return consumer;

    }

}
