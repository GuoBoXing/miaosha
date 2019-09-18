package com.imooc.miaosha.util;

import org.springframework.util.DigestUtils;

import java.io.InputStream;

public class MD5Util {
    public static String md5(String src){
        return DigestUtils.md5DigestAsHex(src.getBytes());
    }

    //固定的salt，为了用户的密码安全
    private static final String salt = "1a2b3c4d";

    //客户端第一次加密
    public static String inputPassFormPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    //服务端向数据库保存，在做一次MD5加密
    public static String formPassToDBPass(String formPass,String saltDB){
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    //客户端向数据库保存MD5加密
    public static String inputPassToDBPass(String input,String saltDB){
        String formPass = inputPassFormPass(input);
        String DBPass = formPassToDBPass(formPass,saltDB);
        return DBPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
    }

}
