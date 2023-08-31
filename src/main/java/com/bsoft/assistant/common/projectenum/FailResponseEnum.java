package com.bsoft.assistant.common.projectenum;

import com.bsoft.assistant.common.exception.BusinessException;
import com.bsoft.assistant.common.utils.StrUtil;

/**
 * 操作失败时返回给前端的数据枚举
 * code：
 * 为数字系列， 位数第一位为需求列表中的模块位置  如数据源排在第一位 取1
 * 总长度为8
 * 第一位表示所在的模块
 * 数据源-1
 * 分析模型-2
 * 仪表板-3
 * 移动端-4
 * 自定义报表-5
 * 门户-6
 * 报表管理-7
 * 权限-8
 * 第二到四位 3位表示service一一对应 不足前面补0
 * 第5位到第8位 4位表示service中的编码，不重复即可
 */
public enum FailResponseEnum {
    //region 共用异常
    COMMON_EMPTY_PARAMS(10000000, "参数为空"),
    ENUM_NAME_NOT_EXIST(10000001, "枚举数据不存在"),
    SYSTEM_ERROR(10000002, "系统后台未知错误！"),
    //endregion
    ;

    FailResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public BusinessException exception() {
        return new BusinessException(this.getCode(), this.getMessage());
    }

    public BusinessException exception(String... args) {
        if (args == null || args.length == 0) return new BusinessException(this.getCode(), this.getMessage());
        return new BusinessException(this.getCode(), StrUtil.strFmt(this.getMessage(),args));
    }



    ;
    //region get set

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    //endregion
}