package com.abc.p2p.mapper;

import com.abc.p2p.model.IncomeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);

    //查询状态为0 到期 的收益计划
    List<IncomeRecord> selectIncomeRecordByStatusAndIncomeDate(int status);
}