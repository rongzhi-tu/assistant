package com.bsoft.assistant.utils;

/**
 * @Author : wt5
 * @Date : Created in 14:20 2020/9/28
 * @Description : 系统常量
 * @Modified By :
 * @Version : 1.0.0
 */
public class SystemConstant {
    public final static String largeField= "_largeField";

    //用于大字段where条件转换的前缀固定字符 (21)
    public final static String where_extract_str = "update abc set a = 1 ";

    //查询的日志 缩小到 5个日志 批处理一次
    public final static int scn_log_shunt_value = 10;

    //oracle类型
    public final static String oracle_type = "oracle";

    //mysql类型
    public final static String mysql_type = "mysql";

    //sqlserver类型
    public final static String sqlserver_type = "sqlserver";

    //sqlServer 16进制常规日志长度最大值
    public final static int sqlServer_data_length = 16000;

    /**
     * EXPLAIN sqlServer操作类型转换通用操作类型标识
     * @return
     */
    public static String sqlServerOperationConvert(String sqlServerOperation){
        String operation = "";
        switch (sqlServerOperation){
            case "LOP_INSERT_ROWS":
                operation= "INSERT";
                break;
            case "LOP_MODIFY_ROW":
                operation= "UPDATE";
                break;
            case "LOP_MODIFY_COLUMNS":
                operation= "UPDATE";
                break;
            case "LOP_DELETE_ROWS":
                operation= "DELETE";
                break;
        }
        return operation;
    }

}
