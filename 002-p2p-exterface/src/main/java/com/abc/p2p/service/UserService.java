package com.abc.p2p.service;

import com.abc.p2p.model.User;

public interface UserService {
    Long queryAllUsers();


    //手机号查询用户
    User queryAllUsersByPhone(String phone);

    User regist(String phone,String loginPassword);

    User login(String phone, String password);

    //修改用户信息
    int modifyUser(User user);

    User checkPhone(String phone);
}
