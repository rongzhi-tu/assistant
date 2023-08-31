package com.bsoft.assistant.common.utils;

import org.springframework.util.StringUtils;

public class DataTypeDealUtils {

    public static Object transType(String type,String val){
        if("int".equals(type)){
           return Integer.parseInt(val);
        }else if("boolean".equals(type)){
            return "1".equals(val)?true:false;
        }
        return StringUtils.isEmpty(val)?"":val;
    }

    public static String transStr(Object val){
        if(val instanceof Boolean){
           return (boolean)val?"1":"0";
        }
        return val.toString();
    }
}
