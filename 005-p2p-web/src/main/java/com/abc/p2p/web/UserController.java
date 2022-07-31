package com.abc.p2p.web;

import com.abc.p2p.Constant.Constant;
import com.abc.p2p.model.FinanceAccount;
import com.abc.p2p.model.User;
import com.abc.p2p.service.FinanceAccountService;
import com.abc.p2p.service.RedisServer;
import com.abc.p2p.service.UserService;
import com.abc.p2p.utils.AuthCode;

import com.abc.p2p.utils.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Reference(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0")
    UserService userService;

    @Reference(interfaceClass = RedisServer.class,timeout = 20000,version = "1.0.0")
    RedisServer redisServer;

    @Reference(interfaceClass = FinanceAccountService.class,timeout = 20000,version = "1.0.0")
    FinanceAccountService financeAccountService;

    @RequestMapping("/loan/page/register")
    public String regist(){
        System.out.println("----regist-----");
        return "register";
    }

    //通过手机号码查询用户信息
    @RequestMapping("/loan/page/checkPhone")
    public Object checkPhone(@RequestParam(value = "phone",required = true) String phone){
        User user = userService.checkPhone(phone);
        if(user != null){
            return Result.error();
        }else{
            return Result.success();
        }
    }




    @RequestMapping("/loan/page/registSubmit")
    @ResponseBody
    public Object registSubmit (@RequestParam (name = "phone",required = true) String phone,
                                @RequestParam(name = "loginPassword", required = true) String loginPassword,
                                @RequestParam(name = "messageCode", required = true) String messageCode,
                                HttpServletRequest request){

        //校验验证码
        String auCode = redisServer.pop(phone);
        if(!StringUtils.equals(auCode,messageCode)){
            return Result.error("验证码错误");
        }


        //注册:插入一条用户记录，送大礼包
        User user = userService.regist(phone,loginPassword);
        if(user != null){
            request.getSession().setAttribute(Constant.LOGIN_USER,user);
            return Result.success();
        }else {
            return Result.error();
        }
    }

    @RequestMapping("/loan/page/messageCode")
    @ResponseBody
    public Object messageCode (@RequestParam(name = "phone",required = true)String phone){

        //生成验证码
        String auCode= AuthCode.generateCode(6);
        //准备好数据
        HashMap<String,Object> parasMap = new HashMap<>();
        parasMap.put("mobile",phone);
        parasMap.put("content","【凯信通】您的验证码是："+auCode);
        parasMap.put("appkey","cd5b89522646433fad8e1c667b95b5d9");


        //模拟报文
        String result="{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 0,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-1111611</remainpoint>\\n <taskID>101609164</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                "}\n";

        //发送给106平台
        try {
            // result=  HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong",parasMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);

        JSONObject jsonObject = JSONObject.parseObject(result);


        //获得code节点
        String code = jsonObject.getString("code");
        if(!StringUtils.equals("10000",code)){
            return Result.error("通信异常");
        }

        String resultXml = jsonObject.getString("result");
        Document document = null;
        try {
            document = DocumentHelper.parseText(resultXml);

        }catch(DocumentException e){
            e.printStackTrace();
        }

        //获得returnstatus节点
        Node node=document.selectSingleNode("//returnstatus");
        String returnstatus=  node.getText();
        if(!StringUtils.equals(returnstatus,"Success")){
            return Result.error("短信发送失败");
        }

        //把短信验证码放入redis中   String类型  k：phone v：auCode
        redisServer.push(phone,auCode);

        return Result.success();

    }



    //登录
    @RequestMapping("/loan/page/login")
    public String login(@RequestParam(name = "ReturnUrl",required = false) String ReturnUrl, Model model){
        System.out.println("--login--"+ReturnUrl);
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "login";

    }


    //登录
    @RequestMapping("/loan/page/loginSubmit")
    @ResponseBody
    public Object loginSumbit(@RequestParam(name = "phone", required = true) String phone,
                              @RequestParam(name = "password", required = true) String password,
                              @RequestParam(name = "messageCode", required = true) String messageCode,
                              HttpServletRequest request){
        System.out.println("--login--");

        //校验：验证码
        String auCode= redisServer.pop(phone);
        if(!StringUtils.equals(auCode,messageCode)){
            return Result.error("验证码输入有误");
        }

        User user = userService.login(phone,password);
        if(user != null){
            request.getSession().setAttribute(Constant.LOGIN_USER,user);
        }else{
            return Result.error("用户名或密码错误");
        }


        //响应前端信息
        return Result.success();

    }



    //退出登录
    @RequestMapping("/loan/page/logout")
    public String logout(HttpServletRequest request){
/*        request.getSession().removeAttribute(Constant.LOGIN_USER);*/

        request.getSession().invalidate();
        return "redirect:/index";

    }

    //小金库
    @RequestMapping("/loan/myCenter")
    public String myCenter(HttpServletRequest request,Model model){
        System.out.println("--myCenter---");
        User user = (User) request.getSession().getAttribute(Constant.LOGIN_USER);
        if(ObjectUtils.allNotNull(user)){
            FinanceAccount financeAccount = financeAccountService.queryMoneyByUid(user.getId());
            model.addAttribute("financeAccount",financeAccount);

        }else{
            return "redirect:/index";
        }



        return "myCenter";
    }

    //跳转到实名认证界面
    @RequestMapping("/loan/page/realName")
    public String realName(){
        System.out.println("---realName---");
        return "realName";
    }

    //实名认证提交
    @RequestMapping("/loan/page/realNameSubmit")
    public Object realNameSubmit(@RequestParam(name = "phone", required = true) String phone,
                                 @RequestParam(name = "realName", required = true) String realName,
                                 @RequestParam(name = "idCard", required = true) String idCard,
                                 @RequestParam(name = "messageCode", required = true) String messageCode,
                                 HttpServletRequest request){
        System.out.println("---realNameSubmit----");


        //校验验证码
        String auCode= redisServer.pop(phone);
        if(!StringUtils.equals(auCode,messageCode)){
            return Result.error("验证码输入有误");
        }

        //实名认证
        //准备好数据

        HashMap<String,Object> parasMap = new HashMap<>();
        parasMap.put("realName",realName);
        parasMap.put("idCard",idCard);
        parasMap.put("appkey","cd5b89522646433fad8e1c667b95b5d9");


        //String result = null;
        //模拟报文
        String result="{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"error_code\": 0,\n" +
                "        \"reason\": \"成功\",\n" +
                "        \"result\": {\n" +
                "            \"realname\": \"乐天磊\",\n" +
                "            \"idcard\": \"350721197702134399\",\n" +
                "            \"isok\": true\n" +
                "        }\n" +
                "    }\n" +
                "}";

        try {
            //result = HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test",parasMap)
        } catch (Exception e) {
            e.printStackTrace();
        }

        //转换为json
        JSONObject jsonObject = JSONObject.parseObject(result);


        //获得通信信息
        String code = jsonObject.getString("code");
        if(!StringUtils.equals(code,"10000")){
            return Result.error("通信异常");
        }

        //获得状态信息
        Boolean isok =  jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");
        if (!isok) {
            return Result.error("请输入正确的身份证号码和姓名");
        }

        //修改用户信息
        User user = (User) request.getSession().getAttribute(Constant.LOGIN_USER);
        int num = userService.modifyUser(user);
        if (num!=1) {
            return Result.error("系统繁忙，请稍后重试");
        }

        return Result.success();

    }

    //根据用户编号查找账户信息
    @RequestMapping("/loan/page/queryMoneyByUid")
    @ResponseBody
    public Object queryMoneyByUid(HttpServletRequest request){
        System.out.println("--queryMoneyByUid--");
        User user = (User) request.getSession().getAttribute(Constant.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return Result.error("身份已过期，请重新登录");
        }

        FinanceAccount financeAccount = financeAccountService.queryMoneyByUid(user.getId());
        return financeAccount;
    }


}
