package com.abc.p2p.utils;

import java.util.HashMap;
import java.util.Map;

public class Result {
    public static Map success(){
        HashMap<String,String> map = new HashMap<>();
        map.put("code","1");
        map.put("msg","ok");
        return map;
    }
    public static Map success(String msg){
        HashMap<String,String> map = new HashMap<>();
        map.put("code","1");
        map.put("msg",msg);
        return map;
    }
    public static Map error(){
        HashMap<String,String> map = new HashMap<>();
        map.put("code","0");
        map.put("msg","error");
        return map;
    }
    public static Map error(String msg){
        HashMap<String,String> map = new HashMap<>();
        map.put("code","0");
        map.put("msg",msg);
        return map;
    }
}
