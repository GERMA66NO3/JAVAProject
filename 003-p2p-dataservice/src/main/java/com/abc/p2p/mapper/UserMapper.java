package com.abc.p2p.mapper;

import com.abc.p2p.model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    Long selectAllUsers();

    //手机号查询用户
    User selectUserByPhone(String phone);

    void insertSelective();


    //根据用户名和密码查询用户
    User selectUserByPhoneAndPassword(@Param("phone") String phone, @Param("password") String password);
}