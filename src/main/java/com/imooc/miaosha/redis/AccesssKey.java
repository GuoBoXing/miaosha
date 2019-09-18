package com.imooc.miaosha.redis;

public class AccesssKey extends BasePrefix {

    public AccesssKey(int expireSenconds, String prefix) {
        super(expireSenconds,prefix);
    }

    public static AccesssKey whithExpire(int expireSenconds){
        return new AccesssKey(expireSenconds,"access");
    }
}
