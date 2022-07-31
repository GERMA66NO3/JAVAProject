package com.bjpowernode.p2p;


import com.abc.p2p.model.RechargeRecord;
import com.abc.p2p.service.IncomeRecordService;
import com.abc.p2p.service.RechargeService;
import com.abc.p2p.utils.HttpClientUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import jdk.jfr.consumer.RecordedClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


//定时器类
@Component
@Slf4j
public class SpringTask {

    @Reference(interfaceClass = IncomeRecordService.class,timeout = 20000,version = "1.0.0")
    IncomeRecordService incomeRecordService;
    @Reference(interfaceClass = RechargeService.class,timeout = 20000,version = "1.0.0.")
    RechargeService rechargeService;

//    @Scheduled(cron = "0/5 * * * * ?")
//    public void test(){
//        log.info("---begin----");
//
//        System.out.println("---执行----");
//
//        log.info("-----end------");
//    }




    //生成收益计划
    @Scheduled(cron = "0/5 * * * * ?")
    public void generatePlan(){
        /*
         * 1、投标产品表中 查找 状态为1 的产品信息（id）
         * 2、投资表中 查找 该产品的投资记录 ==》生成收益计划  状态为0
         * 3、该产品的状态改为2 （满标已生成收益计划）
         *
         */


        incomeRecordService.generatePlan();

    }

    //收益计划返现
    @Scheduled(cron = "0/5 * * * * ?")
    public void generatePlanToBack(){
        /*
         * 1、到期的没有返现的收益计划 进行返现（本金+利息）
         * 2、账户余额增加 （现在余额+本金+利息）
         * 3、收益计划 状态更新为1
         *
         */

        incomeRecordService.generatePlanToBack();

    }

    //监控状态为0的订单，是否已经充值成功   =  解决丢单问题 （1、延迟定时器执行周期  2、乐观锁）
    @Scheduled(cron = "0/5 * * * * ?")
    public void checkRechargeRecord(){
        /*
         * 1、查询状态为0的订单
         * 2、每个状态为0的订单，到支付宝查询交易信息
         * 3、修改订单状态 和账户余额
         */

        //1、查询状态为0的订单
        List<RechargeRecord> rechargeRecordList =  rechargeService.queryRechargeRecordByStatus("0");
        for(RechargeRecord rechargeRecord : rechargeRecordList){
            Map<String,String> map = new HashMap<>();
            map.put("out_trade_no",rechargeRecord.getRechargeNo());
            String  result=null;
            try {
                //保证定时器继续执行下去
                result= HttpClientUtils.doGet("http://localhost:8007/007-p2p-pay/pay/alipayQuery",map);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(ObjectUtils.allNotNull(result)){
                //解析
                JSONObject jsonObject = JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");
                String code=jsonObject.getString("code");
                if(StringUtils.equals(code,"10000")){
                    String trade_status=jsonObject.getString("trade_status");
                    if(StringUtils.equals(trade_status,"TRADE_CLOSED")){
                        //修改订单状态为2  充值失败
                        //课后帮我实现

                    }

                    if(StringUtils.equals(trade_status,"TRADE_SUCCESS")){

                        /*
                         *  //修改订单状态为1  充值成功
                         *  //账户余额增加
                         */
                        HashMap<String,Object> parasMap=new HashMap<>();
                        parasMap.put("rechargeNo",rechargeRecord.getRechargeNo());
                        parasMap.put("uid",rechargeRecord.getUid());
                        parasMap.put("totalAmount",rechargeRecord.getRechargeMoney());
                        parasMap.put("rechargeStatus","1");
                        //修改订单状态为1  充值成功
//                        synchronized (){
//
//                        }
                        //根据订单号查询订单 ，查看订单状态如果为0，继续执行
                        rechargeRecord=  rechargeService.queryRechargeRecordByrechargeNo(rechargeRecord.getRechargeNo());
                        if(StringUtils.equals("0",rechargeRecord.getRechargeStatus())){
                            rechargeService.updateRecordStatusAndFinanceAccount(parasMap);
                        }

                    }

                }

            }


        }


    }


}
