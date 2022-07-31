package com.abc.p2p.web;

import com.abc.p2p.utils.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WxpayController {

    @RequestMapping("/pay/wxpay")
    @ResponseBody
    public Object wxpay(@RequestParam(name = "rechargeNo", required = true) String rechargeNo,
                        @RequestParam(name = "total_fee", required = true) Double total_fee,
                        @RequestParam(name = "body", required = true) String body)throws Exception{
        Map<String, String> map = new HashMap<>();
        map.put("appid", "wx8a3fcf509313fd74");
        map.put("mch_id", "1361137902");
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        map.put("body", body);
        map.put("out_trade_no",rechargeNo);
        BigDecimal bigDecimal= new BigDecimal(total_fee);
        //java.math.BigDecimal.multiply(BigDecimal被乘数)是java中的一个内置方法，该方法返回一个BigDecimal，其值是(this×被乘数)
        BigDecimal bigDecimal1= bigDecimal.multiply(new BigDecimal(100));
        map.put("total_fee", bigDecimal1.intValue()+"");
        map.put("spbill_create_ip", "127.0.0.1");
        map.put("notify_url", "locahost");
        map.put("trade_type", "NATIVE");
        map.put("product_id", rechargeNo);

        String sign = WXPayUtil.generateSignature(map, "367151c5fd0d50f1e34a68a802d6bbca");
        map.put("sign", sign);
        String xml = WXPayUtil.mapToXml(map);
        return WXPayUtil.xmlToMap( HttpClientUtils.doPostByXml("https://api.mch.weixin.qq.com/pay/unifiedorder", xml));
    }
}
