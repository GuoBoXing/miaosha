package com.imooc.miaosha.access;

import com.imooc.miaosha.domain.MiaoShaUser;

public class UserContext {
    //每个线程单独保存一份，不涉及线程安全问题
    private static ThreadLocal<MiaoShaUser> userHolder = new ThreadLocal<>();

    public static void setUser(MiaoShaUser user){
        userHolder.set(user);
    }

    public static MiaoShaUser getUser(){
        return userHolder.get();
    }

}
