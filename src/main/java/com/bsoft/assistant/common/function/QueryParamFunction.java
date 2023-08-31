package com.bsoft.assistant.common.function;


import com.bsoft.assistant.common.model.CommonQueryParam;
import java.util.List;

/**
 * Created by
 * Administrator
 * on 2022/1/24.
 */
@FunctionalInterface
public interface QueryParamFunction {
    List<CommonQueryParam> initQueryParam();
}
