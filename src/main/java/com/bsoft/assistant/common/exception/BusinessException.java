package com.bsoft.assistant.common.exception;

import com.bsoft.assistant.common.projectenum.FailResponseEnum;
import ctd.util.exception.CodedBaseException;

/**
 * Created by
 * Administrator
 * on 2021/12/8.
 */
public class BusinessException extends CodedBaseException {

    public BusinessException(int code, String msg) {
        super(code, msg);
    }

    public BusinessException(FailResponseEnum failResponseEnum) {
        super(failResponseEnum.getCode(), failResponseEnum.getMessage());
    }
}
