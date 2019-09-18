package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoShaUserDao;
import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.MiaoShaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoShaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoShaUserDao miaoShaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoShaUser getById(long id){
        return miaoShaUserDao.getById(id);
    }

    public MiaoShaUser getByToken(HttpServletResponse response,String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        MiaoShaUser miaoShaUser = redisService.get(MiaoShaUserKey.token, token, MiaoShaUser.class);
        //延长有效期
        if (miaoShaUser != null) {
            addCookie(response,token,miaoShaUser);
        }
        return miaoShaUser;
    }
    public boolean login(HttpServletResponse response, LoginVo loginVo) {

        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        //判断手机号是否存在
        MiaoShaUser miaoShaUser = miaoShaUserDao.getById(Long.parseLong(mobile));
        if(miaoShaUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIS);
        }
        //验证密码
        String dbPass = miaoShaUser.getPassword();
        String salfDB = miaoShaUser.getSalf();
        String calcPass = MD5Util.formPassToDBPass(formPass,salfDB);
        if(!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response,token,miaoShaUser);
        return true;
    }
    //添加cookie
    public void addCookie(HttpServletResponse response, String token,MiaoShaUser miaoShaUser){
        redisService.set(MiaoShaUserKey.token,token,miaoShaUser);

        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoShaUserKey.token.expreSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }


}
