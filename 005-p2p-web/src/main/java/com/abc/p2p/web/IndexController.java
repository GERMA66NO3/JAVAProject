package com.abc.p2p.web;

import com.abc.p2p.Constant.Constant;
import com.abc.p2p.model.BidInfo;
import com.abc.p2p.service.BidInfoService;
import com.abc.p2p.service.LoanService;
import com.abc.p2p.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abc.p2p.Constant.Constant.*;


@Controller
public class IndexController {

    @Reference(interfaceClass = LoanService.class,timeout = 20000,version = "1.0.0")
    LoanService loanService;

    @Reference(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0")
    UserService userService;

    @Reference(interfaceClass = BidInfoService.class,timeout = 20000,version = "1.0.0")
    BidInfoService bidInfoService;



    @RequestMapping("/index")
    public String index(Model model){
        //动力金融网历史年化收益率
        Double avgLoanInfoRate = loanService.queryAvgLoanInfoRate();
        model.addAttribute(Constant.AVG_LOAN_INFO_RATE,avgLoanInfoRate);


        //平台人数
        Long allUsers = userService.queryAllUsers();
        model.addAttribute(Constant.ALL_USERS,allUsers);

        //累计成交额
        Long bidInfo = bidInfoService.queryBidInfo();
        model.addAttribute(Constant.BID_INFO,bidInfo);


        Map<String,Object> paramMap = new HashMap<String, Object>();

        //新手宝
        paramMap.put("productType","0");
        paramMap.put("start",0);
        paramMap.put("content",1);
        model.addAttribute(LOAN_INFO_X,loanService.queryLoanInfo(paramMap));


        //查询优选标

        paramMap.put("productType","1");
        paramMap.put("start",0);
        paramMap.put("content",4);
        model.addAttribute(LOAN_INFO_Y,loanService.queryLoanInfo(paramMap));



        //查询散标
        paramMap.put("productType","2");
        paramMap.put("start",0);
        paramMap.put("content",8);
        model.addAttribute(LOAN_INFO_S,loanService.queryLoanInfo(paramMap));



        return "index";
    }


}
