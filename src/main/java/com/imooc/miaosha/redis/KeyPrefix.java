package com.imooc.miaosha.redis;

/***
 * 定义接口
 */
public interface KeyPrefix {

    public int expreSeconds();

    public String getPrefix();
}
