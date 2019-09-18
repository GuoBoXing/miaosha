package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.domain.MiaoShaUser;

public class MiaoshaMessage {
    private MiaoShaUser user;
    private Long id;

    public MiaoShaUser getUser() {
        return user;
    }

    public void setUser(MiaoShaUser user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
