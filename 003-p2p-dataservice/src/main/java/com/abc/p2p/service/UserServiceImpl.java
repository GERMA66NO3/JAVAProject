package com.abc.p2p.service;


import com.abc.p2p.Constant.Constant;
import com.abc.p2p.mapper.FinanceAccountMapper;
import com.abc.p2p.mapper.UserMapper;
import com.abc.p2p.model.FinanceAccount;
import com.abc.p2p.model.User;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0")
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    FinanceAccountMapper financeAccountMapper;

    @Override
    public Long queryAllUsers() {

        Long allUsers = (Long) redisTemplate.opsForValue().get(Constant.ALL_USERS);

        if(allUsers ==null){
            synchronized(this){
                allUsers = (Long) redisTemplate.opsForValue().get(Constant.ALL_USERS);
                if(allUsers == null){
                    allUsers = userMapper.selectAllUsers();
                    redisTemplate.opsForValue().set(Constant.ALL_USERS,allUsers,10, TimeUnit.SECONDS);
                }
            }

        }
        return allUsers;
    }

    //手机号查询用户
    @Override
    public User queryAllUsersByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);

    }

    //注册，送大礼包
    @Override
    @Transactional
    public User regist(String phone, String loginPassword) {
        //新增用户记录
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        userMapper.insertSelective();

        //送大礼包
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setAvailableMoney(888d);
        financeAccount.setUid(user.getId());
        financeAccountMapper.insertSelective(financeAccount);
        return user;


    }

    @Override
    public User login(String phone, String password) {
        User user = userMapper.selectUserByPhoneAndPassword(phone,password);
        if(user!=null){

            //如果查询成功，修改登录时间
            user.setLastLoginTime(new Date());


            new Thread(new Runnable() {
                @Override
                public void run() {
                    userMapper.updateByPrimaryKeySelective(user);
                }
            }).start();
        }
        return null;
    }

    //修改用户信息
    @Override
    public int modifyUser(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User checkPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }
}
