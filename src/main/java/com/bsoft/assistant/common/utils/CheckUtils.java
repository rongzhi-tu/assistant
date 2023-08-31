package com.bsoft.assistant.common.utils;

import com.bsoft.assistant.common.exception.BusinessException;
import com.bsoft.assistant.common.function.CommonCheckFunction;
import com.bsoft.assistant.common.projectenum.FailResponseEnum;
import org.springframework.util.CollectionUtils;
import java.util.Collection;
import java.util.Map;

/**
 * Created by
 * tuz
 * on 2021/12/8.
 * BI业务检验工具类
 */
public class CheckUtils {

    /**
     *  equal比较校验
     * @param param : 校验的数据
     * @param value ：比较的值
     * @param failResponseEnum：检验匹配后失败的提示枚举信息
     * @throws BusinessException ：检验匹配后失败的提示枚举信息对应的异常
     */
    public static void throwExceptionIfParamEq(Object param, Object value, FailResponseEnum failResponseEnum, String ... args) throws BusinessException {
        if (value.equals(param)) throw failResponseEnum.exception(args);
    }

    /**
     *  ！equal比较校验
     * @param param : 校验的数据
     * @param value ：比较的值
     * @param failResponseEnum：检验匹配后失败的提示枚举信息
     * @throws BusinessException ：检验匹配后失败的提示枚举信息对应的异常
     */
    public static void throwExceptionIfParamNotEq(Object param, Object value,FailResponseEnum failResponseEnum,String ... args) throws BusinessException {
        if (!value.equals(param)) throw failResponseEnum.exception(args);
    }
    /**
     *  null值比较校验
     * @param value : 校验的数据
     * @param failResponseEnum：检验匹配后失败的提示枚举信息
     * @throws BusinessException ：检验匹配后失败的提示枚举信息对应的异常
     */
    public static void throwExceptionIfNull(Object value,FailResponseEnum failResponseEnum,String ... args) throws BusinessException {
        if (value == null) throw failResponseEnum.exception(args);
    }

    /**
     *  map 为空的比较校验
     * @param value ：比较的值
     * @param failResponseEnum：检验匹配后失败的提示枚举信息
     * @throws BusinessException ：检验匹配后失败的提示枚举信息对应的异常
     */
    public static void throwExceptionIfEmpty(Map value, FailResponseEnum failResponseEnum,String ... args) throws BusinessException {
        if (CollectionUtils.isEmpty(value)) throw failResponseEnum.exception(args);
    }
    /**
     *  collection 为空的比较校验
     * @param value ：比较的值
     * @param failResponseEnum：检验匹配后失败的提示枚举信息
     * @throws BusinessException ：检验匹配后失败的提示枚举信息对应的异常
     */
    public static void throwExceptionIfEmpty(Collection value, FailResponseEnum failResponseEnum,String ... args) throws BusinessException {
        if (CollectionUtils.isEmpty(value)) throw failResponseEnum.exception(args);
    }

    /**
     *  collection 不为空的比较校验
     * @param value ：比较的值
     * @param failResponseEnum：检验匹配后失败的提示枚举信息
     * @throws BusinessException ：检验匹配后失败的提示枚举信息对应的异常
     */
    public static void throwExceptionIfNotEmpty(Collection value, FailResponseEnum failResponseEnum,String ... args) throws BusinessException {
        if (!CollectionUtils.isEmpty(value)) throw failResponseEnum.exception(args);
    }

    /**
     *  map 不为空的比较校验
     * @param value ：比较的值
     * @param failResponseEnum：检验匹配后失败的提示枚举信息
     * @throws BusinessException ：检验匹配后失败的提示枚举信息对应的异常
     */
    public static void throwExceptionIfNotEmpty(Map value, FailResponseEnum failResponseEnum,String ... args) throws BusinessException {
        if (!CollectionUtils.isEmpty(value)) throw failResponseEnum.exception(args);
    }

    /**
     *  万能检验api
     * @param checkFunction : 通用的检验规则
     * @param failResponseEnum ：检验匹配后失败的提示枚举信息
     * @throws BusinessException ：检验匹配后失败的提示枚举信息对应的异常
     */
    public static void throwExceptionCheckNotPass(CommonCheckFunction checkFunction, FailResponseEnum failResponseEnum, String ... args) throws BusinessException {
        if (checkFunction.check()) throw failResponseEnum.exception(args);
    }
}
