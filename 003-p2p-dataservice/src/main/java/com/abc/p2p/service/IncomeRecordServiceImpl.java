package com.abc.p2p.service;

import com.abc.p2p.mapper.BidInfoMapper;
import com.abc.p2p.mapper.FinanceAccountMapper;
import com.abc.p2p.mapper.IncomeRecordMapper;
import com.abc.p2p.mapper.LoanInfoMapper;
import com.abc.p2p.model.BidInfo;
import com.abc.p2p.model.IncomeRecord;
import com.abc.p2p.model.LoanInfo;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = IncomeRecordService.class,timeout = 20000,version = "1.0.0")
@Component
//收益实现类
public class IncomeRecordServiceImpl implements IncomeRecordService {
    @Autowired
    LoanInfoMapper loanInfoMapper;

    @Autowired
    BidInfoMapper bidInfoMapper;

    @Autowired
    IncomeRecordMapper incomeRecordMapper;

    @Autowired
    FinanceAccountMapper financeAccountMapper;


    @Transactional
    @Override
    //生成收益计划
    public void generatePlan() {
        /*
         * 1、投标产品表中 查找 状态为1 的产品信息（id）
         * 2、投资表中 查找 该产品的投资记录 ==》生成收益计划  状态为0
         * 3、该产品的状态改为2 （满标已生成收益计划）
         *
         */


        //1、投标产品表中 查找 状态为1 的产品信息（id）
        List<LoanInfo> loanInfos = loanInfoMapper.selectLoanInfoByStatus(1);

        //遍历集合，拿到每一个产品

        for(LoanInfo loanInfo : loanInfos){
            //根据产品的编号，到投资表中获得该产品的所有投资记录
            List<BidInfo> bidInfos = bidInfoMapper.selectAllRecordByLoanId(loanInfo.getId());

            //遍历投资记录，每条投资记录 =》收益计划  状态0
            for(BidInfo bidInfo : bidInfos){
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setBidId(bidInfo.getId());
                //投资金额
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                //获得收益的时间   收益时间= 当前时间（满标时间）+周期时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(loanInfo.getProductFullTime());
                if(loanInfo.getProductStatus() == 0){
                    //新手宝
                    calendar.add(Calendar.DAY_OF_YEAR,loanInfo.getCycle());

                }else {
                    calendar.add(Calendar.MONTH,loanInfo.getCycle());
                }

                incomeRecord.setIncomeDate(calendar.getTime());

                //收益金额
                incomeRecord.setIncomeMoney(0d);
                if(loanInfo.getProductStatus() == 0){
                    //新手宝
                    incomeRecord.setIncomeMoney(Math.round(loanInfo.getRate()/100/365*loanInfo.getCycle()*bidInfo.getBidMoney())/100d);
                }else{
                    incomeRecord.setIncomeMoney(Math.round(loanInfo.getRate()/100/365*loanInfo.getCycle()*30*bidInfo.getBidMoney())/100d);

                }
                //收益金额状态
                incomeRecord.setIncomeStatus(0);
                //获取产品ID
                incomeRecord.setLoanId(loanInfo.getId());
                //用户ID
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecordMapper.insertSelective(incomeRecord);
            }
            //修改该产品的状态为2
            loanInfo.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(loanInfo);


        }

    }


    //收益计划返现
    @Transactional
    public void generatePlanToBack(){

        /*
         * 1、到期的没有返现的收益计划 进行返现（本金+利息）
         * 2、账户余额增加 （现在余额+本金+利息）
         * 3、收益计划 状态更新为1
         *
         */

        //查询状态为0 到期 的收益计划

        List<IncomeRecord> incomeRecords = incomeRecordMapper.selectIncomeRecordByStatusAndIncomeDate(0);
        //遍历收益计划，获取每条收益计划
        for(IncomeRecord incomeRecord : incomeRecords){
            HashMap<String,Object> parasMap = new HashMap<>();
            parasMap.put("bidMoney",incomeRecord.getBidMoney());
            parasMap.put("incomeMoney",incomeRecord.getIncomeMoney());
            parasMap.put("uid",incomeRecord.getUid());

            //返现给账户
            int num = financeAccountMapper.updateAccountByIncomeMoney(parasMap);
            if(num!=1){
                //抛异常
                throw  new RuntimeException("返现失败");

            }
            //收益计划 状态更新为1
            incomeRecord.setIncomeStatus(1);
            incomeRecordMapper.updateByPrimaryKeySelective(incomeRecord);


        }


        //收益计划 状态更新为1


    }
}
