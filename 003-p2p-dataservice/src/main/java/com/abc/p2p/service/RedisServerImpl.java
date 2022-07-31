package com.abc.p2p.service;

import com.abc.p2p.Constant.Constant;
import com.abc.p2p.vo.InvestTopVo;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass = RedisServer.class,timeout = 20000,version = "1.0.0")
@Component
public class RedisServerImpl implements RedisServer {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Override
    public void push(String phone, String auCode) {
        //把电话号和验证码放入redis，过期时间为1分钟
        redisTemplate.opsForValue().set(phone,auCode,1, TimeUnit.MINUTES);
    }

    //根据key获得验证码
    @Override
    public String pop(String phone) {
        return (String) redisTemplate.opsForValue().get(phone);
    }


    //用户账号，投资总金额，存入缓存中
    @Override
    public void zpush(String phone, Double bidMoney) {
        redisTemplate.opsForZSet().incrementScore(Constant.INVSET_TOP,phone,bidMoney);


    }

    //从缓存中获取投资排行的数据
    @Override
    public List<InvestTopVo> zpop(int num) {
        Set<ZSetOperations.TypedTuple> set = redisTemplate.opsForZSet().reverseRangeByScore(Constant.INVSET_TOP, 0, num);
        List<InvestTopVo> investTopVos = new ArrayList<InvestTopVo>();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            InvestTopVo investTopVo = new InvestTopVo();
            ZSetOperations.TypedTuple zt =it.next();
            investTopVo.setPhone(zt.getValue()+"");
            investTopVo.setBidmMoney(zt.getScore());
            investTopVos.add(investTopVo);
        }
        return investTopVos;
    }

    //用于生成主键自增字段
    public Long generateNum(){
        return redisTemplate.opsForValue().increment(Constant.INCR_NUMBER,1);
    }

}
