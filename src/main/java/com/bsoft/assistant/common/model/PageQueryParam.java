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
public class PageQueryParam implements Serializable{
    private int pageNo = 1;
    private int pageSize = 10;

    List<CommonQueryParam> commonQueryParamList;

    public PageQueryParam initInstance(int pageNo, int pageSize, QueryParamFunction queryParamFunction){
        this.setPageNo(pageNo);
        this.setPageSize(pageSize);
        this.setCommonQueryParamList(queryParamFunction.initQueryParam());
        return this;
    }

    public static PageQueryParam init(int pageNo, int pageSize, QueryParamFunction queryParamFunction){
        PageQueryParam result = new PageQueryParam();
        result.initInstance(pageNo,pageSize,queryParamFunction);
        return result;
    }
}
