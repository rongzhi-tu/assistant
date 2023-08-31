package com.bsoft.assistant.common.model;

import com.bsoft.assistant.common.function.QueryParamFunction;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * Created by
 * tuz
 * on 2021/12/14.
 */
@Data
public class QueryParam implements Serializable{

    List<CommonQueryParam> commonQueryParamList;

    public QueryParam initInstance(QueryParamFunction queryParamFunction){
        this.setCommonQueryParamList(queryParamFunction.initQueryParam());
        return this;
    }

    public static QueryParam init(QueryParamFunction queryParamFunction){
        QueryParam result = new QueryParam();
        result.initInstance(queryParamFunction);
        return result;
    }
}
