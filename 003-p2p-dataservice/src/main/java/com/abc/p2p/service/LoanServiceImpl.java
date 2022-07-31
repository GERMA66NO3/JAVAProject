package com.abc.p2p.service;

import com.abc.p2p.Constant.Constant;
import com.abc.p2p.mapper.LoanInfoMapper;
import com.abc.p2p.model.LoanInfo;
import com.abc.p2p.utils.PageModel;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass=LoanService.class,timeout=20000,version="1.0.0")
@Component
public class LoanServiceImpl implements LoanService {

    @Autowired
    LoanInfoMapper loanInfoMapper;

    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public Double queryAvgLoanInfoRate() {

        Double avgLoanInfoRate = (Double) redisTemplate.opsForValue().get(Constant.AVG_LOAN_INFO_RATE);
        if(avgLoanInfoRate == null){
            synchronized (this){
                avgLoanInfoRate = (Double) redisTemplate.opsForValue().get(Constant.AVG_LOAN_INFO_RATE);
                if(avgLoanInfoRate == null){
                    avgLoanInfoRate =  loanInfoMapper.selectAvgLoanInfoRate();
                    redisTemplate.opsForValue().set(Constant.AVG_LOAN_INFO_RATE,avgLoanInfoRate,10, TimeUnit.SECONDS);
                }
            }
        }
        return avgLoanInfoRate;

    }

    @Override
    public List<LoanInfo> queryLoanInfo(Map<String, Object> paramMap) {

        return loanInfoMapper.selectLoanInfo();


    }

    @Override
    public List<LoanInfo> queryLoanInfoByType(Integer ptype, PageModel pageModel) {

        Map<String ,Object> parasMap = new HashMap<>();
        parasMap.put("start",(pageModel.getCurPage()-1) * pageModel.getPageContent());
        parasMap.put("content",pageModel.getPageContent());
        parasMap.put("ptype",ptype);


        return loanInfoMapper.selectLoanInfoByType(parasMap);

    }

    @Override
    public Integer queryLoanInfoTotalCount(Integer ptype) {
        return loanInfoMapper.selectLoanInfoTotalCount(ptype);
    }

    @Override
    public LoanInfo queryLoanInfoByLoanId(Integer loanId) {
        return loanInfoMapper.selectByPrimaryKey(loanId);
    }


}
