package com.abc.p2p.service;

import com.abc.p2p.mapper.FinanceAccountMapper;
import com.abc.p2p.mapper.RechargeRecordMapper;
import com.abc.p2p.model.RechargeRecord;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service(interfaceClass = RechargeService.class,timeout = 20000,version = "1.0.0")
@Component
public class RechargeServiceImpl implements RechargeService {

    @Autowired
    RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    FinanceAccountMapper financeAccountMapper;


    //支付宝充值
    @Override
    public int alipay(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    @Override
    @Transactional
    //修改订单状态和账户余额
    public int updateRecordStatusAndFinanceAccount(HashMap<String, Object> paramsMap) {
        int num = rechargeRecordMapper.updateRechargeRecord(paramsMap);
        if(num!=1){
            return -1;
        }

        if(num == 1){
            num = financeAccountMapper.updateAccountByUserIdAndMoney(paramsMap);
        }
        if(num!=1){
            return -1;
        }

        return num;

    }

    @Override
    public int updateRecordStatus(HashMap<String, Object> paramsMap) {
        return rechargeRecordMapper.updateRechargeRecord(paramsMap);
    }

    //根据订单号 查询订单
    @Override
    public RechargeRecord queryRechargeRecordByrechargeNo(String out_trade_no) {
        return rechargeRecordMapper.selectRechargeRecordByRechargeNo(out_trade_no);

    }

    @Override
    public List<RechargeRecord> queryRechargeRecordByStatus(String status) {
        return rechargeRecordMapper.selectRechargeRecordByStatus(status);
    }
}
