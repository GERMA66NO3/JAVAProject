package com.abc.p2p.web;

import com.abc.p2p.Constant.Constant;
import com.abc.p2p.model.BidInfo;
import com.abc.p2p.model.FinanceAccount;
import com.abc.p2p.model.LoanInfo;
import com.abc.p2p.model.User;
import com.abc.p2p.service.BidInfoService;
import com.abc.p2p.service.FinanceAccountService;
import com.abc.p2p.service.LoanService;
import com.abc.p2p.service.RedisServer;
import com.abc.p2p.utils.PageModel;
import com.abc.p2p.vo.InvestTopVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class LoanController {
    @Reference(interfaceClass = LoanService.class,timeout = 20000,version = "1.0.0")
    LoanService loanService;

    @Reference(interfaceClass = BidInfoService.class,timeout = 20000,version = "1.0.0")
    BidInfoService bidInfoService;


    @Reference(interfaceClass = FinanceAccountService.class,timeout = 20000,version = "1.0.0")
    FinanceAccountService financeAccountService;

    @Reference(interfaceClass = RedisServer.class,version = "1.0.0",timeout = 20000)
    RedisServer redisServer;

    @RequestMapping("/loan/loan")
    public String loan(@RequestParam(name = "ptype",required = false) Integer ptype,
                       @RequestParam(name = "curPage",required = true) Integer curPage,
                       Model model){

        PageModel pageModel = new PageModel(9);
        pageModel.setCurPage(curPage);
        Integer count = loanService.queryLoanInfoTotalCount(ptype);
        pageModel.setTotalCount(count);

        if(curPage<pageModel.getFirstPage()){
            curPage = 1;

        }

        if (curPage > pageModel.getLastPage()){
            curPage = pageModel.getLastPage();
        }

        List<LoanInfo> loanInfos = loanService.queryLoanInfoByType(ptype,pageModel);
        model.addAttribute("loanInfos",loanInfos);
        model.addAttribute("ptype",ptype);
        model.addAttribute("pageModel",pageModel);


        //投资排行榜
        List<InvestTopVo> investTopVos =     redisServer.zpop(5);
        model.addAttribute(Constant.INVSET_TOP,investTopVos);




        return "loan";
    }

    @RequestMapping("/loan/loanInfo")
    public String loanInfo(@RequestParam(name = "loanId",required = true) Integer loanId, Model model, HttpServletRequest request){

        //查找当前产品信息
        LoanInfo loanInfo = loanService.queryLoanInfoByLoanId(loanId);
        model.addAttribute("loanInfo",loanInfo);

        //查找当前产品投资记录
        List<BidInfo> bidInfos =  bidInfoService.queryBidInfoByLoanId(loanId);
        model.addAttribute("bidInfos",bidInfos);

        //如果已经登录，获取账户信息
        User user = (User) request.getSession().getAttribute(Constant.LOGIN_USER);

        if(ObjectUtils.allNotNull(user)){
            FinanceAccount financeAccount = financeAccountService.queryMoneyByUid(user.getId());
            model.addAttribute("financeAccount",financeAccount);
        }


        return "loanInfo";
    }

}
