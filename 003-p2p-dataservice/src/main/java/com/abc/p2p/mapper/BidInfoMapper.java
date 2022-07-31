package com.abc.p2p.mapper;

import com.abc.p2p.model.BidInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    //根据产品编号，获得该产品所有投资信息
    List<BidInfo> selectAllRecordByLoanId(Integer id);

    //根据产品ID，查询投资记录
    List<BidInfo> selectBidInfoRecordByLoanId(Integer loanId);
}