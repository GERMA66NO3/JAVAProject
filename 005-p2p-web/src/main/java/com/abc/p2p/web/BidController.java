package com.abc.p2p.web;

import com.abc.p2p.Constant.Constant;
import com.abc.p2p.model.User;
import com.abc.p2p.service.BidInfoService;
import com.abc.p2p.service.RedisServer;
import com.abc.p2p.utils.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

//投资控制层
public class BidController {


    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 20000)
    BidInfoService bidInfoService;
    @Reference(interfaceClass = RedisServer.class,version = "1.0.0",timeout = 20000)
    RedisServer redisServer;
    //投资
    @RequestMapping("/loan/page/invest")
    public Object invest(
            @RequestParam(name = "loanId",required = true) Integer loanId,
            @RequestParam(name = "bidMoney",required = true) Double bidMoney,HttpServletRequest request
    ){
        //判断用户是否已经登录
        User user = (User) request.getSession().getAttribute(Constant.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return Result.error("请先登录，再投资");
        }



        //投资
        /*  1.账户余额减少（账户余额是否充足）
        *   2.产品可投资金额减少
        *   3.添加投资记录
        *   4.判断产品是否满标
        * */
        Map<String,Object> parasMap = new HashMap<String,Object>();
        parasMap.put("uid",user.getId());
        parasMap.put("loanId",loanId);
        parasMap.put("bidMoney",bidMoney);
        try {
            String result = bidInfoService.invest(parasMap);
            if(StringUtils.equals(result,Constant.INVSET_AVAILABLE_MONEY)){
                return Result.error("您的账户余额不足");
            }
            if(StringUtils.equals(result,Constant.INVSET_LEFT_PRODUCT_MONEY)){
                return Result.error("您投资的金额超过产品剩余可投金额..");
            }
            if(StringUtils.equals(result,Constant.INVSET_BID_INFO)){
                return Result.error("投资记录添加失败..");
            }
            if(StringUtils.equals(result,Constant.INVSET_LOAN_STATUS)){
                return Result.error("投标产品状态更新异常..");
            }
            if(StringUtils.equals(result,Constant.INVSET_SUCCESS)){
                //插入一条记录到redis中
                redisServer.zpush(user.getPhone(),bidMoney);
                return Result.success();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("投资失败");
            
        }
        return Result.success();

    }
}
