package com.abc.p2p.mapper;

import com.abc.p2p.model.FinanceAccount;
import com.abc.p2p.model.IncomeRecord;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public interface FinanceAccountMapper {

    //更新充值后的账户余额
    int updateAccountByUserIdAndMoney(HashMap<String, Object> paramsMap);

    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    FinanceAccount selectAccountByUid(Integer uid);

    int updateMoneyByUidAndBidMoney(Map<String, Object> parasMap);

    //返现本金+利息
    Integer updateAccountByIncomeMoney(HashMap<String, Object> parasMap);
}