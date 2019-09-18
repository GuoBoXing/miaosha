package com.imooc.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final String MIAOSHA_QUEUE_NAME = "miaosha.queue";
    public static final String QUEUE_NAME = "queue";
    public static final String TOPIC_QUEUE_NAME1 = "topic.queue1";
    public static final String TOPIC_QUEUE_NAME2 = "topic.queue2";
    public static final String HEADER_QUEUE_NAME = "header.queue";
    public static final String TOPIC_EXCHANGE = "topicExchange";
    public static final String FANOUT_EXCHANGE = "fanoutExchange";
    public static final String HEADERS_EXCHANGE = "headersExchange";

    public static final String ROUTING_KEY1 = "topic.key1";
    public static final String ROUTING_KEY2 = "topic.#";

    /**
     * Direct模式，交换机Exchange
     */
    @Bean
    public Queue queue(){
        return new Queue(QUEUE_NAME,true);
    }

    @Bean
    public Queue queue1(){
        return new Queue(MIAOSHA_QUEUE_NAME,true);
    }
    /**
     * topic模式，交换机Exchange
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE_NAME1,true);
    }

    /**
     * topic模式，交换机Exchange
     */
    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE_NAME2,true);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);
    }

    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);
    }
    /**
     * fanout模式，交换机Exchange
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding fanoutBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }

    /**
     * Header模式，交换机Exchange
     */
    @Bean
    public HeadersExchange headerExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Queue headerQueue1(){
        return new Queue(HEADER_QUEUE_NAME,true);
    }


    @Bean
    public Binding headerBinding(){
        Map<String,Object> map = new HashMap<String,Object >();
        map.put("header1","value1");
        map.put("header2","value2");
        return BindingBuilder.bind(topicQueue1()).to(headerExchange()).whereAll(map).match();
    }
}
