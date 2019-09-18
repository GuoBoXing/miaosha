package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {
    private static Logger logger = LoggerFactory.getLogger(MQSender.class);
    @Autowired
    AmqpTemplate amqpTemplate;

    public void senderMiaoshaMessage(MiaoshaMessage message) {
        String msg = RedisService.beanToString(message);
        logger.info("sender Message"+msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE_NAME,msg);
    }

    /*public void send(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("sender Message"+msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE_NAME,msg);
    }

    public void sendTopic(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("sender Message"+msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+1);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+2);
    }

    public void sendFanout(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("sender Message"+msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg+1);
    }

    public void sendHeader(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("sender Message"+msg);

        MessageProperties properties = new MessageProperties();
        properties.setHeader("header1","value1");
        properties.setHeader("header2","value2");
        Message obj = new Message(msg.getBytes(),properties);

        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
    }*/
}
