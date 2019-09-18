package com.imooc.miaosha.vo;

import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoShaUser;

public class GoodsDetailVo {
    private int miaoshaStatus = 0 ;
    private int remainSeconds = 0;
    private Goods goods;
    private MiaoShaUser miaoShaUser;

    public MiaoShaUser getMiaoShaUser() {
        return miaoShaUser;
    }

    public void setMiaoShaUser(MiaoShaUser miaoShaUser) {
        this.miaoShaUser = miaoShaUser;
    }

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }
}
