package com.imooc.miaosha.redis;

/**
 * 抽象类，做一些通用的实现，既然是抽象类肯定要有成员变量
 */
public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;

    private String prefix;

    //永不过期
    public BasePrefix (String prefix){
        this(0,prefix);
    }

    public BasePrefix (int expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    /**
     * 过期时间，0默认代表永不过期
     * @return
     */
    @Override
    public int expreSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        //为了避免模块设置的值重复，通过类名来区分
        String className = getClass().getSimpleName();
        return className + ":" +prefix;
    }
}
