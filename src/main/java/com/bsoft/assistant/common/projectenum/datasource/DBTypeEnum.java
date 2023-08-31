package com.bsoft.assistant.common.projectenum.datasource;

import com.bsoft.assistant.common.projectenum.IResourceEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2022/1/21.
 */
public enum DBTypeEnum implements IResourceEnum<String, String> {

    MYSQL("mysql","mysql"),
    ORACLE("oracle","oracle"),
    SQLSERVER("sql server","sql server"),
//    DB2("db2",4),
//    HBASE("HBASE",5),
//    EXCEL("EXCEL",6)
    ;

    private String code;
    private String text;


    // 构造方法
    private DBTypeEnum(String text, String code) {
        this.text = text;
        this.code = code;
    }


    public static String getName(String index) {
        for (DBTypeEnum c : DBTypeEnum.values()) {
            if (c.getCode() == index) {
                return c.text;
            }
        }
        return null;
    }

    private static final Map<String, DBTypeEnum> enums = new HashMap<>();
    static {
        enums.put(MYSQL.getCode(), MYSQL);
        enums.put(ORACLE.getCode(), ORACLE);
        enums.put(SQLSERVER.getCode(),SQLSERVER);
//        enums.put(DB2.getCode(),DB2);
//        enums.put(HBASE.getCode(),HBASE);
//        enums.put(EXCEL.getCode(),EXCEL);
    }
    public static DBTypeEnum parse(String code) {
        return enums.getOrDefault(code, null);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
