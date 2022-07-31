package com.abc.p2p.utils;
/**
 * @ClassName AuthCode
 * @Description 生成验证码类
 * @author PowerNode
 * @Date 2020/11/19 11:34
 * @version 1.0
 */
public class AuthCode {

    //生成验证码
    public static  String  generateCode(int size){

        StringBuilder sb=new StringBuilder();
        for(int i=0;i<size;i++){
            sb.append( Math.round(Math.random()*9));
        }
        return sb.toString();
    }

}
