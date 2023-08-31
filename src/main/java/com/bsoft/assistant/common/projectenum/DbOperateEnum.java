package com.bsoft.assistant.common.projectenum;



import com.bsoft.assistant.common.utils.DBUtils;
import com.bsoft.assistant.common.model.CommonQueryParam;
import com.bsoft.assistant.constants.Const;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.eclipse.jetty.util.StringUtil;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by
 * tuz
 * on 2022/1/24.
 */
public enum DbOperateEnum {

    EQ("="),NE("<>"), GT(">"), GE(">="), LT("<"), LE("<="), LIKE(Const.LIKE_EXPRESSION),
    BETWEEN_AND(Const.BETWEEN_EXPRESSION),
    IN(Const.IN);


    private String expression;

    DbOperateEnum(String expression) {
        this.expression = expression;
    }

    public void setCriteria(Criteria criteria,CommonQueryParam commonQueryParam) {
        String fieldName = commonQueryParam.getFieldName();
        if (Const.BETWEEN_EXPRESSION.equals(expression)) {
            if (commonQueryParam.getDateStart() != null && commonQueryParam.getDataEnd() != null) {
                criteria.add(Restrictions.between(fieldName, commonQueryParam.getDateStart(),commonQueryParam.getDataEnd()));
            }
        }else { //EQ/LIKE
            if (commonQueryParam.getFieldValue() != null) {
                if(commonQueryParam.getFieldValue().equals("") &&
                        (this.expression.equals(EQ.expression) || this.expression.equals(LIKE.expression))) return ;

                if (LIKE.expression.equals(expression)){
                    criteria.add(Restrictions.like(fieldName, DBUtils.decodeLikeValue(commonQueryParam.getFieldValue().toString())));
                }else if (EQ.expression.equals(expression)){
                    criteria.add(Restrictions.eq(fieldName, commonQueryParam.getFieldValue()));
                }else if (IN.expression.equals(expression)){
                    criteria.add(Restrictions.in(fieldName, commonQueryParam.getFieldValue()));
                }else if (NE.expression.equals(expression)){
                    criteria.add(Restrictions.ne(fieldName, commonQueryParam.getFieldValue()));
                }else if (GT.expression.equals(expression)){
                    criteria.add(Restrictions.gt(fieldName, commonQueryParam.getFieldValue()));
                }else if (GE.expression.equals(expression)){
                    criteria.add(Restrictions.ge(fieldName, commonQueryParam.getFieldValue()));
                }else if (LT.expression.equals(expression)){
                    criteria.add(Restrictions.lt(fieldName, commonQueryParam.getFieldValue()));
                }else if (LE.expression.equals(expression)){
                    criteria.add(Restrictions.le(fieldName, commonQueryParam.getFieldValue()));
                }
            }
        }
    }
    public String sqlParamSegment(CommonQueryParam commonQueryParam, Map<String, Object> params) {
        String result = "";
        String fieldName = commonQueryParam.getFieldName();
        if (Const.BETWEEN_EXPRESSION.equals(expression)) {
            if (commonQueryParam.getDateStart() != null && commonQueryParam.getDataEnd() != null) {
                result = String.format(" and %s between :%s and :%s", fieldName, fieldName + "_start", fieldName + "_end");
                params.put(fieldName + "_start", commonQueryParam.getDateStart());
                params.put(fieldName + "_end", commonQueryParam.getDataEnd());
            }
        }else{
            if (commonQueryParam.getFieldValue() != null) {
                if(commonQueryParam.getFieldValue().equals("") && this.expression.equals(EQ.expression)) return result;

                result = String.format(" and %s %s :%s", fieldName, this.expression, fieldName);
                if (Const.LIKE_EXPRESSION.equals(expression)){
                    params.put(fieldName, DBUtils.decodeLikeValue(commonQueryParam.getFieldValue().toString()));
                }else{
                    params.put(fieldName, commonQueryParam.getFieldValue());
                }

            }
        }
        return result;
    }
    private static final Map<String, DbOperateEnum> enums = new HashMap<>();
    static {
        enums.put("EQ", EQ);
        enums.put("NE", NE);
        enums.put("GT", GT);
        enums.put("GE", GE);
        enums.put("LT", LT);
        enums.put("LE", LE);
        enums.put("LIKE", LIKE);
        enums.put("BETWEEN_AND", BETWEEN_AND);
        enums.put("IN", IN);
    }
    public static DbOperateEnum getEnum(String code) {
        return enums.getOrDefault(code, null);
    }

    /**
     * 分析模型获取数据源sql参数使用
     * @param commonQueryParam
     * @param params
     * @return
     */
    public String sqlParamSegmentPre(CommonQueryParam commonQueryParam, ArrayList<Object> params) {
        String result = "";
        String fieldName = commonQueryParam.getFieldName();
        if (Const.BETWEEN_EXPRESSION.equals(expression)) {
            if (commonQueryParam.getDateStart() != null && commonQueryParam.getDataEnd() != null) {
                result = String.format(" and %s between ? and ?", fieldName);
                params.add( commonQueryParam.getDateStart());
                params.add( commonQueryParam.getDataEnd());
            }
        }else{
            if (commonQueryParam.getFieldValue() != null) {
                if(commonQueryParam.getFieldValue().equals("") && this.expression.equals(EQ.expression)) return result;

                result = String.format(" and %s %s ?", fieldName, this.expression);
                if (Const.LIKE_EXPRESSION.equals(expression)){
                    params.add( DBUtils.decodeLikeValue(commonQueryParam.getFieldValue().toString()));
                }else if(Const.IN.equals(expression)){
                    String[] objects = changeArray(commonQueryParam.getFieldValue());

                    StringBuilder sb=new StringBuilder();
                    if(objects.length==0) return "";
                    for (String str : objects) {
                        if(StringUtil.isBlank(sb.toString())){
                            sb.append("?");
                        }else{
                            sb.append(",?");
                        }
                        params.add(str);
                    }
                    result = String.format(" and %s %s ( %s )", fieldName, this.expression,sb);
                }else{
                    params.add(commonQueryParam.getFieldValue());
                }
            }
        }
        return result;
    }
    public static String[] changeArray(Object json) {
        Gson gson=new Gson();
        String[] res=null;
        try {
            res=gson.fromJson(json.toString(), new TypeToken<String[]>() {
            }.getType());
        } catch (Exception e){
        }
        return res;
    }


    public String getExpression() {
        return expression;
    }
}
