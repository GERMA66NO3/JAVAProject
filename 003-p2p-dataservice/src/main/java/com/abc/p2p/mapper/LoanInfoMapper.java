package com.abc.p2p.mapper;

import com.abc.p2p.model.LoanInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface LoanInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoanInfo record);

    int insertSelective(LoanInfo record);

    LoanInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LoanInfo record);

    int updateByPrimaryKey(LoanInfo record);

    //产品可投金额减少
    Integer updateMoneyByLoanId(Map<String, Object> parasMap);

    //查找状态为1的产品
    List<LoanInfo> selectLoanInfoByStatus(int i);
}