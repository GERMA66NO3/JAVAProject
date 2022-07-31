package com.abc.p2p.mapper;

import com.abc.p2p.model.RechargeRecord;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);

    //修改订单状态
    int updateRechargeRecord(HashMap<String, Object> paramsMap);

    //根据订单号 查询订单
    RechargeRecord selectRechargeRecordByRechargeNo(String out_trade_no);
    //查询订单状态为0的订单
    List<RechargeRecord> selectRechargeRecordByStatus(String status);
}