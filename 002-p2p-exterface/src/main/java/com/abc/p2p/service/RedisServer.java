package com.abc.p2p.service;

import com.abc.p2p.vo.InvestTopVo;

import java.util.List;

public interface RedisServer {
    //把验证码放入redis中，key是电话号码，value是验证码
    void push(String phone, String auCode);

    //根据key键获得验证码
    String pop(String phone);

    //用户账号，投资总金额，存入缓存中
    void zpush(String phone, Double bidMoney);

    //从缓存中获取投资排行的数据
    List<InvestTopVo> zpop(int i);

    //用于生成主键自增字段
    Long generateNum();
}
