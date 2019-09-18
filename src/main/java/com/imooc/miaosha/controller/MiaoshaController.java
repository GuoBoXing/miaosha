package com.imooc.miaosha.controller;

import com.imooc.miaosha.access.AccessLimit;
import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.rabbitmq.MiaoshaMessage;
import com.imooc.miaosha.redis.AccesssKey;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoShaUserService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 把redis清空
 数据库清空 再运行
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{

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

    @Autowired
    MQSender mqSender;

    private Map<Long,Boolean> localOverMap = new HashMap<Long,Boolean>();

    /**
     * 系统初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null){
            return;
        }
        for (GoodsVo goodsVo : goodsVoList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goodsVo.getId(),goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(),false);
        }
    }

    /***
     * 分布式session，就是把session放到缓存里，单独管理起来，通过cookie里的信息去缓存里获取session
     * @param model
    //     * @param cookieToken 客户端的cookie
    //     * @param paraToken request里的cookie
     * @return
     */
    /*
        GET 幂等
        POST 非幂等
     */
    @RequestMapping(value = "/{path}/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoShaUser miaoShaUser, @RequestParam("goodsId") Long goodsId,
                                   @PathVariable("path")String path) {
        model.addAttribute("user", miaoShaUser);
        if (miaoShaUser == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean chek = miashaService.checkPath(miaoShaUser,goodsId,path);

        if (!chek){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //预减库存
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        // 判断是否已经秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoShaUser.getId(),goodsId);
        if (miaoshaOrder != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //入队
        MiaoshaMessage message = new MiaoshaMessage();
        message.setUser(miaoShaUser);
        message.setId(goodsId);
        mqSender.senderMiaoshaMessage(message);

        return Result.success(0); //排队中
        /*//判断库存
        GoodsVo goodsVo = goodsService.getByGoodsVoGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if (stock <= 0){
            model.addAttribute("errMsg", CodeMsg.MIAOSHA_OVER.getMsg());
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        // 判断是否已经秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoShaUser.getId(),goodsId);
        if (miaoshaOrder != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        // 减库存，下订单，写入秒杀订单
        OrderInfo orderInfo = miashaService.miaosha(miaoShaUser,goodsVo);
        return Result.success(orderInfo);
        */
    }

    /**
     *
     * orderId ; 成功
     * -1：秒杀失败
     * @param model
     * @param miaoShaUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoShaUser miaoShaUser, @RequestParam("goodsId") Long goodsId) {
        model.addAttribute("user", miaoShaUser);
        if (miaoShaUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = miashaService.getMiaoshaResult(miaoShaUser.getId(),goodsId);
        return Result.success(result);
    }


    //验证码验证
    @AccessLimit(seconds=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request,MiaoShaUser miaoShaUser, @RequestParam("goodsId") Long goodsId,
                                         @RequestParam(value = "verifyCode",defaultValue = "0")int verifyCode) {
        if (miaoShaUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = miashaService.checkVerifyCode(miaoShaUser,goodsId,verifyCode);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        String path = miashaService.createMiaoshaPath(miaoShaUser,goodsId);
        return Result.success(path);
    }

    //渲染图片
    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, MiaoShaUser miaoShaUser, @RequestParam("goodsId") Long goodsId) {
        if (miaoShaUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = miashaService.createVerifyCode(miaoShaUser,goodsId);
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"JPEG",out) ;
            out.flush();
            out.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SESSION_ERROR);
        }
    }
}

