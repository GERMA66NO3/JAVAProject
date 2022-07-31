package com.abc.p2p.service;

import com.abc.p2p.Constant.Constant;
import com.abc.p2p.mapper.BidInfoMapper;
import com.abc.p2p.mapper.FinanceAccountMapper;
import com.abc.p2p.mapper.LoanInfoMapper;
import com.abc.p2p.model.BidInfo;
import com.abc.p2p.model.LoanInfo;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass = BidInfoService.class,timeout = 20000,version = "1.0.0")
@Component
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired(required = false)
    BidInfoMapper bidInfoMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    FinanceAccountMapper financeAccountMapper;

    @Autowired
    LoanInfoMapper loanInfoMapper;


    @Override
    public Long queryBidInfo() {

        Long bidInfo = (Long) redisTemplate.opsForValue().get(Constant.BID_INFO);
        if(bidInfo == null){
            synchronized (this){
                bidInfo = (Long) redisTemplate.opsForValue().get(Constant.BID_INFO);
                if(bidInfo==null){
                    bidInfo = bidInfoMapper.selectBidInfo();
                    redisTemplate.opsForValue().set(Constant.BID_INFO,bidInfo,10, TimeUnit.SECONDS);

                }
            }
        }
        return bidInfo;

    }

    @Override
    public List<BidInfo> queryBidInfoByLoanId(Integer loanId) {
        return bidInfoMapper.selectBidInfoRecordByLoanId(loanId);
    }


    //投资
    @Override
    @Transactional
    public String invest(Map<String, Object> parasMap) {
        //投资
        /*  1.账户余额减少（账户余额是否充足）
         *   2.产品可投资金额减少
         *   3.添加投资记录
         *   4.判断产品是否满标
         * */

        //根据用户编号查找账户信息


        int num = 0;
        //账户余额减少(判断账户余额是否充足)

        //根据前端商品的ID获取该商品的最新的所有信息
        LoanInfo loan = loanInfoMapper.selectByPrimaryKey((Integer) parasMap.get("loanId"));
        //获取该商品的版本号
        parasMap.put("version",loan.getVersion());

        num = financeAccountMapper.updateMoneyByUidAndBidMoney(parasMap);
        if(num == 0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Constant.INVSET_AVAILABLE_MONEY;
        }
        //产品可投金额减少
        num = loanInfoMapper.updateMoneyByLoanId(parasMap);
        if(num == 0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Constant.INVSET_LEFT_PRODUCT_MONEY;
        }
        //添加投资记录
        BidInfo bidInfo = new BidInfo();
        bidInfo.setLoanId((Integer) parasMap.get("loanId"));
        bidInfo.setUid((Integer) parasMap.get("uid"));
        bidInfo.setBidMoney((Double) parasMap.get("bidMoney"));
        bidInfo.setBidTime(new Date());
        bidInfo.setBidStatus(0);
        num = bidInfoMapper.insertSelective(bidInfo);
        if(num == 0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Constant.INVSET_BID_INFO;
        }

        //判断产品是否满标(可用余额是否==0),以及满标时间
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey((Integer) parasMap.get("loanId"));
        if(loanInfo.getProductMoney() == 0 && loanInfo.getProductStatus() == 0){
            loanInfo.setProductStatus(1);
            loanInfo.setProductFullTime(new Date());
            num = loanInfoMapper.updateByPrimaryKeySelective(loanInfo);
            if(num == 0){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Constant.INVSET_LOAN_STATUS;
            }
        }
        return Constant.INVSET_SUCCESS;


    }
}
