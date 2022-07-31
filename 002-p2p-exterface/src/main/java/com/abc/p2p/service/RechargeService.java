package com.abc.p2p.service;

import com.abc.p2p.model.RechargeRecord;

import java.util.HashMap;
import java.util.List;

public interface RechargeService {

    //充值支付
    int alipay(RechargeRecord rechargeRecord);

    //修改订单状态和账户余额
    int updateRecordStatusAndFinanceAccount(HashMap<String, Object> paramsMap);

    //修改订单状态
    int updateRecordStatus(HashMap<String, Object> paramsMap);

    //根据订单号 查询订单
    RechargeRecord queryRechargeRecordByrechargeNo(String out_trade_no);

    //查询订单状态为0的订单
    List<RechargeRecord> queryRechargeRecordByStatus(String status);
}
