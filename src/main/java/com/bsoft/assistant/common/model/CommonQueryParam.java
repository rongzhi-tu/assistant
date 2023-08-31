package com.bsoft.assistant.common.model;

import com.bsoft.assistant.common.projectenum.DbOperateEnum;
import lombok.Data;
import java.util.Date;

/**
 * Created by
 * tuz
 * on 2022/1/24.
 */
@Data
public class CommonQueryParam {
    String fieldName;
    Object fieldValue;
    Date dateStart;
    Date dataEnd;
    String expression;
    DbOperateEnum dbOperateEnum = DbOperateEnum.EQ;

    public CommonQueryParam(String fieldName, Object fieldValue, DbOperateEnum dbOperateEnum) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.dbOperateEnum = dbOperateEnum;
    }

    public CommonQueryParam(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public CommonQueryParam(String fieldName, Date dateStart, Date dataEnd) {
        this.fieldName = fieldName;
        this.dateStart = dateStart;
        this.dataEnd = dataEnd;
    }
    public CommonQueryParam() {

    }
}
