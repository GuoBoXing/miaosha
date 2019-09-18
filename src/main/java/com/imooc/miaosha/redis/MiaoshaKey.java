package com.imooc.miaosha.redis;

public class MiaoshaKey extends BasePrefix {

    public MiaoshaKey(int expireSenconds,String prefix) {
        super(expireSenconds,prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0,"go");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60,"mp");
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300,"vc");
}
