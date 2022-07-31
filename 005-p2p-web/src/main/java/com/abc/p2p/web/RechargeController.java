package com.abc.p2p.web;

import com.abc.p2p.Constant.Constant;
import com.abc.p2p.config.AlipayConfig;
import com.abc.p2p.model.RechargeRecord;
import com.abc.p2p.model.User;
import com.abc.p2p.service.RechargeService;
import com.abc.p2p.service.RedisServer;
import com.abc.p2p.utils.HttpClientUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class RechargeController {


    @Reference(interfaceClass = RechargeService.class,timeout = 20000, version = "1.0.0")
    RechargeService rechargeService;
    @Reference(interfaceClass = RedisServer.class,timeout = 20000, version = "1.0.0")
    RedisServer redisServer;

    //跳转充值页面
    @RequestMapping("/loan/page/toRecharge")
    public String toRecharge(){
        System.out.println("--toRecharge--");
        return "toRecharge";
    }

    @RequestMapping("/loan/page/alipay")
    public String alipay(@RequestParam(name = "rechargeMoney",required = true)Double rechargeMoney, HttpServletRequest request, Model model){
        //判断用户是否登录
        User user = (User) request.getSession().getAttribute(Constant.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return "redirect:/loan/page/login";
        }

        //生成订单：未支付（0）
        RechargeRecord rechargeRecord = new RechargeRecord();
        //设置充值ID
        rechargeRecord.setUid(user.getId());
        //设置充值数额
        rechargeRecord.setRechargeMoney(rechargeMoney);
        //设置充值日期
        Date date = new Date();
        rechargeRecord.setRechargeTime(date);
        //设置充值单号
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String rechargeNo = simpleDateFormat.format(date);
        //生成唯一的订单号
        rechargeRecord.setRechargeNo(rechargeNo+user.getId()+redisServer.generateNum());
        //设置充值描述description
        rechargeRecord.setRechargeDesc("支付宝充值");
        //设置充值状态
        rechargeRecord.setRechargeStatus("0");


        try {
            int num = rechargeService.alipay(rechargeRecord);
            model.addAttribute("rechargeRecord", rechargeRecord);
            if (num != 1) {
                model.addAttribute("trade_msg", "充值失败");
            }

        } catch (Exception e) {
            model.addAttribute("trade_msg", "充值失败");
            e.printStackTrace();
        }


        //这种方法直接把数据带到地址栏，不安全
        //return "redirect:http://localhost:8007/007-p2p-pay/alipay?out_trade_no="+rechargeNo+"subject"+rechargeNo+"total_amount"+rechargeMoney+"body"+rechargeRecord.getRechargeDesc();


        //去005的payToAlipay页面，自动提交订单信息
        return "payToAlipay";
    }


    //页面跳转同步通知页面
    @RequestMapping("/loan/page/aliPayBack")
    public String aliPayBack(HttpServletRequest request,Model model) throws UnsupportedEncodingException, AlipayApiException {
        /* *
         * 功能：支付宝服务器同步通知页面
         * 日期：2017-03-30
         * 说明：
         * 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
         * 该代码仅供学习和研究支付宝接口使用，只是提供一个参考。


         *************************页面功能说明*************************
         * 该页面仅做页面展示，业务逻辑处理请勿在该页面执行
         */


        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        //调用SDK验证签名：保证数据的安全
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            System.out.println("--验证通过--");
            //查询订单状态

            //获得初始化的AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

            //设置请求参数
            AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();

            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

            alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","+"\"trade_no\":\""+ trade_no +"\"}");


            //真正的result来源
            //result = alipayClient.execute(alipayRequest).getBody();



            //模拟的result(供开发时用)
            String result = "{\n" +
                    "    \"alipay_trade_query_response\":{\n" +
                    "        \"code\":\"10000\",\n" +
                    "        \"msg\":\"Success\",\n" +
                    "        \"buyer_logon_id\":\"ajc***@sandbox.com\",\n" +
                    "        \"buyer_pay_amount\":\"0.00\",\n" +
                    "        \"buyer_user_id\":\"2088622955042551\",\n" +
                    "        \"buyer_user_type\":\"PRIVATE\",\n" +
                    "        \"invoice_amount\":\"0.00\",\n" +
                    "        \"out_trade_no\":\"2020112609540735\",\n" +
                    "        \"point_amount\":\"0.00\",\n" +
                    "        \"receipt_amount\":\"0.00\",\n" +
                    "        \"send_pay_date\":\"2020-11-26 09:54:23\",\n" +
                    "        \"total_amount\":\"200.00\",\n" +
                    "        \"trade_no\":\"2020112622001442550500882148\",\n" +
                    "        \"trade_status\":\"TRADE_SUCCESS\"\n" +
                    "    },\n" +
                    "    \"sign\":\"mvICYAYrczsjjlioqI1q52TaP1EArmsZ+uaJysF0ZYbg5hfBo2mygeqqvLsAZT0T7Hgj+DnoyXE7HhFCINSFPzY3KuN1E41Ld4CpLBgnkmJO0Jtzk29fG3lT11J1wfv8lhIOO1atsRdCXGWwdhPeJ7U1Vw4/o76l0q2nwRUMsNywvZnmulqDiIt3HXkSRLv1pVxhZEoepdPhktL3qDKsq7Rr6fUdrP6dFd6PgAiuTdHkpyo5dfWczwNFsjUYxOSJR7nj6hNac+GGM5DqQ2C7Cvdr/SZJ/5m8neULGcbTzhC9e50G5uKuf/OkvSXlxlAQ4ch6faM9+CgJNy84EqkDJA==\"\n" +
                    "}";

            Map<String,String> map = new HashMap<>();
            map.put("out_trade_no",out_trade_no);

            try {
                //请求pay工程 查询订单信息，请求参数是map里的out_trade_no
                result = HttpClientUtils.doGet("http://localhost:8007/007-p2p-pay/pay/alipayQuery", map);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("trade_msg", "账户异常,请联系客服");
                return "toRechargeBack";
            }

            //解析
            JSONObject jsonObject = JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");
            String code = jsonObject.getString("code");
            if(!StringUtils.equals(code,"10000")){
                model.addAttribute("trade_msg","通信失败");
                return "toRechargeBack";
            }

            String tradeStatus = jsonObject.getString("trade_status");
            if(StringUtils.equals(tradeStatus,"TRADE_CLOSED")){
                //修改订单状态为2  充值失败
                //课后帮我实现
                User user = (User) request.getSession().getAttribute(Constant.LOGIN_USER);
                HashMap<String,Object> paramsMap = new HashMap<>();
                paramsMap.put("rechargeNo",out_trade_no);
                paramsMap.put("rechargeStatus","2");
                paramsMap.put("uid",user.getId());
                int num = rechargeService.updateRecordStatus(paramsMap);
                if(num == 1){
                    model.addAttribute("trade_msg", "充值失败，订单状态已修改，请稍后重试");
                    return "toRechargeBack";
                }else {
                    model.addAttribute("trade_msg", "账户异常，请联系客服");
                    return "toRechargeBack";
                }

            }

            if(StringUtils.equals(tradeStatus,"TRADE_SUCCESS")){
                //修改订单状态为1， 充值成功
                //账户余额增加
                User user = (User) request.getSession().getAttribute(Constant.LOGIN_USER);
                HashMap<String,Object> paramsMap = new HashMap<>();
                paramsMap.put("rechargeNo",out_trade_no);
                paramsMap.put("uid",user.getId());
                paramsMap.put("total_amount",total_amount);
                paramsMap.put("rechargeStatus","1");
                try {
                    //修改订单状态为1， 充值成功
                    //先根据订单号查询一下订单 ，查看订单状态如果为0，继续执行（防止定时器已经提前做过一边，造成充值了两次）
                    RechargeRecord rechargeRecord = rechargeService.queryRechargeRecordByrechargeNo(out_trade_no);
                    if(StringUtils.equals("0",rechargeRecord.getRechargeStatus())){
                        int num = rechargeService.updateRecordStatusAndFinanceAccount(paramsMap);
                        if(num == -1){
                            model.addAttribute("trade_msg", "账户异常，请联系客服");
                            return "toRechargeBack";
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    model.addAttribute("trade_msg", "账户异常，请联系客服");
                    return "toRechargeBack";
                }

                //充值成功，跳转小金库界面
                return "redirect:/loan/myCenter";
            }

        }
        model.addAttribute("trade_msg", "数据异常,请再次充值");
        return "toRechargeBack";
    }




    @RequestMapping("/loan/page/wxpay")
    public String wxpay(@RequestParam(name = "rechargeMoney",required = true)Double rechargeMoney,HttpServletRequest request,Model model){
        System.out.println("---wxpay---");

        //判断用户是否登录
        User user = (User) request.getSession().getAttribute(Constant.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return "redirect:/loan/page/login";
        }

        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setUid(user.getId());
        Date date = new Date();
        rechargeRecord.setRechargeTime(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String rechargeNo = simpleDateFormat.format(date);
        rechargeRecord.setRechargeNo(rechargeNo+user.getId()+redisServer.generateNum());
        rechargeRecord.setRechargeDesc("微信充值");
        rechargeRecord.setRechargeStatus("0");

        try {
            int num = rechargeService.alipay(rechargeRecord);
            model.addAttribute("rechargeRecord", rechargeRecord);
            if (num != 1) {
                model.addAttribute("trade_msg", "充值失败");
            }

        } catch (Exception e) {
            model.addAttribute("trade_msg", "充值失败");
            e.printStackTrace();
        }
        return "wxToPay";
    }


    @RequestMapping("/loan/page/generateQRCode")
    public ModelAndView generateQRCode(HttpServletResponse response,@RequestParam(name = "rechargeNo",required = true) String rechargeNo) throws Exception {

        RechargeRecord rechargeRecord = rechargeService.queryRechargeRecordByrechargeNo(rechargeNo);
        HashMap<String,Object> parasMap = new HashMap<>();
        parasMap.put("rechargeNo",rechargeNo);
        parasMap.put("total_fee",rechargeRecord.getRechargeMoney());
        parasMap.put("body",rechargeRecord.getRechargeDesc());
        String result = HttpClientUtils.doPost("http://localhost:8007/007-p2p-pay/pay/wxpay",parasMap);
        System.out.println(result);

        JSONObject jsonObject = JSONObject.parseObject(result);
        String return_code=jsonObject.getString("return_code");
        if (StringUtils.equals("SUCCESS",return_code)){
            String result_code=jsonObject.getString("result_code");
            if(StringUtils.equals("SUCCESS",result_code)){
                String code_url=jsonObject.getString("code_url");
                if(ObjectUtils.allNotNull(code_url)){
                    //设置字符集
                    Map<EncodeHintType,Object> map= new HashMap<>();
                    map.put(EncodeHintType.CHARACTER_SET,"UTF-8");
                    //创建一个矩阵对象
                    BitMatrix bitMatrix=new MultiFormatWriter().encode(code_url, BarcodeFormat.QR_CODE,200,200, map);

                    //将矩阵对象转换为二维码图片
                    MatrixToImageWriter.writeToStream(bitMatrix,"jpg",response.getOutputStream());

                }
            }
        }
        return  null;

    }
}
