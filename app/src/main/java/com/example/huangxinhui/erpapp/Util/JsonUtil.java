package com.example.huangxinhui.erpapp.Util;


import com.alibaba.fastjson.JSONObject;

public class JsonUtil {
    public static boolean isJson(String str) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
