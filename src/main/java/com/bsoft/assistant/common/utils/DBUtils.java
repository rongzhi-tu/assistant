package com.bsoft.assistant.common.utils;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by
 * trz
 * on 2021/7/6.
 * 数据库操作上的工具类封装
 */
public class DBUtils {
    private static final String LIKE_CHAR = "%";
    private static final String SELECT_FMT_XING = "select * from %s";
    private static final String PARA_CHAR = "'";
    public static String decodeLikeValue(String value){
        return LIKE_CHAR + value + LIKE_CHAR;
    }

    /**
     * sql拼接符
     * @param value
     * @return String
     */
    public static String decodeSqlValue(String value){
        return PARA_CHAR + value + PARA_CHAR;
    }

    /**
     *  通过表名，及选择的字段列构造出基本的查询SQL语句
     * @param tableName ： 表名
     * @param columns 字段列表
     * @return ： 返回SQL片段 eg: select col,col2.... from <tableName>
     */
    public static String generateSelectSqlSegement(String tableName, String... columns) {
        return generateSelectSqlSegement(tableName, Arrays.asList(columns));
    }
    /**
     *  通过表名，及选择的字段列构造出基本的查询SQL语句
     * @param tableName ： 表名
     * @param selectColumns 字段列表
     * @return ： 返回SQL片段 eg: select col,col2.... from <tableName>
     */
    public static String generateSelectSqlSegement(String tableName, List<String> selectColumns) {
        if (CollectionUtils.isEmpty(selectColumns)) return String.format(SELECT_FMT_XING, tableName);

        StringBuilder stringBuilder = new StringBuilder("select ");
        for (String col : selectColumns) {
            stringBuilder.append(col).append(" as \"").append(col).append("\",");
        }
        //将最后一个,换成空格
        stringBuilder = stringBuilder.replace(stringBuilder.lastIndexOf(","), stringBuilder.length(), " ");
        stringBuilder.append("from ").append(tableName);
        return stringBuilder.toString();
    }

    /**
     * in 表达式安全处理，当超过1000长度切分成段  否则不特殊处理
     * @param fieldNameExpression：字段表达式 如table1.name
     * @param inDatas : in数据
     * @param param ： 分割时提供的参数值信息
     * @return ： 返回in表达式对应的占位表达式  eg: table1.name in (:field1) or table2.name in (:field2)
     */
    public static String InSqlSegmentInit(String fieldNameExpression, List<Object> inDatas, Map<String, Object> param) {
        if (StringUtils.isEmpty(fieldNameExpression) || CollectionUtils.isEmpty(inDatas) || param == null) return "0=1";

        int len = inDatas.size();
        int segLen = 990; //990
        if (len >= 1000) { //1000
            int fromIndex = 0;
            int toIndex = segLen;
            StringBuilder stringBuilder = new StringBuilder("(");
            while (toIndex < len) {
                String paramValue = "field" + toIndex;
                if (fromIndex == 0) {
                    String segmentSqlFmt = String.format("%s in (:%s) ", fieldNameExpression, paramValue);
                    stringBuilder.append(segmentSqlFmt);
                } else {
                    String segmentSqlFmt = String.format("or %s in (:%s) ", fieldNameExpression, paramValue);
                    stringBuilder.append(segmentSqlFmt);
                }
                param.put(paramValue, inDatas.subList(fromIndex, toIndex));
                fromIndex = toIndex;
                toIndex += segLen;
                if (toIndex > len) toIndex = len;
            }
            if (fromIndex < len) {
                String paramValue = "field" + toIndex;
                String segmentSqlFmt = String.format("or %s in (:%s) ", fieldNameExpression, paramValue);
                stringBuilder.append(segmentSqlFmt);
                param.put(paramValue, inDatas.subList(fromIndex, len));
            }
            stringBuilder.append(")");
            return stringBuilder.toString();
        } else {
            String paramValue = "field" + new Date().getTime();
            String segmentSqlFmt = String.format("%s in (:%s)", fieldNameExpression, paramValue);
            param.put(paramValue, inDatas);
            return segmentSqlFmt;
        }
    }
}
