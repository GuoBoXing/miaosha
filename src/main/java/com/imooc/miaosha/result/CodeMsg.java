package com.imooc.miaosha.result;

import lombok.Data;

@Data
public class CodeMsg {
    private int code;
    private String msg;

    //通用异常
    public static  CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static  CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static  CodeMsg BIND_ERROR = new CodeMsg(500100,"参数校验异常: %s");
    public static  CodeMsg REQUEST_ILLEGAL  = new CodeMsg(500102,"请求非法");
    public static  CodeMsg ACCESS_LIMIT_RECHED  = new CodeMsg(500103,"请求太频繁！");
    //登录模块 5002xx
    public static  CodeMsg SESSION_ERROR = new CodeMsg(500210,"session不存在或已经失效");
    public static  CodeMsg PASSWORD_EMPTY = new CodeMsg(500211,"登陆密码不能为空");
    public static  CodeMsg MOBILE_EMPTY = new CodeMsg(500212,"手机号不能为空");
    public static  CodeMsg MOBILE_ERROR = new CodeMsg(500213,"手机号码格式错误");
    public static  CodeMsg MOBILE_NOT_EXIS  = new CodeMsg(500214,"用户不存在");
    public static  CodeMsg PASSWORD_ERROR  = new CodeMsg(500214,"密码错误");
    //商品模块 5003xx

    //订单模块 5004xx
    public static  CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400,"订单不存在");
    //秒杀模块 5005xx
    public static  CodeMsg MIAOSHA_OVER  = new CodeMsg(500500,"商品售罄（qing）");
    public static  CodeMsg REPEATE_MIAOSHA  = new CodeMsg(500501,"您已秒杀过了");
    public static  CodeMsg MIAOSHA_FAIL  = new CodeMsg(500502,"秒杀失败");


    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg,args);
        return new CodeMsg(code,message);
    }

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
