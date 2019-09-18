package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoShaUserService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miashaService;

    @Autowired
    RedisService redisService;

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE_NAME)
    public void receive(String message) {
        logger.info("receive message"+message);
        MiaoshaMessage miaoshaMessage = RedisService.StringToBean(message, MiaoshaMessage.class);
        MiaoShaUser user = miaoshaMessage.getUser();
        Long goodsId = miaoshaMessage.getId();

        GoodsVo goods = goodsService.getByGoodsVoGoodsId(goodsId);//10个商品，req1，req2
        int stock = goods.getGoodsStock();
        if (stock <= 0){
            return;
        }
        // 判断是否已经秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if (miaoshaOrder != null) {
            return ;
        }
        //减库存，下订单，写入秒杀订单
        miashaService.miaosha(user,goods);
    }


   /* @RabbitListener(queues = MQConfig.QUEUE_NAME)
    public void receive(String message){
        logger.info("receive message"+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE_NAME1)
    public void receivetopic1(String message){
        logger.info("receive topic1 message"+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE_NAME2)
    public void receivetopic2(String message){
        logger.info("receive topic2 message"+message);
    }

    @RabbitListener(queues = MQConfig.HEADER_QUEUE_NAME)
    public void receiveHeaderQueue(byte[] message){
        logger.info("header topic2 message"+new String(message));
    }*/
}
