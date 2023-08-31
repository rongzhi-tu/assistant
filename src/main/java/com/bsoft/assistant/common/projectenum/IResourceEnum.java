package com.bsoft.assistant.common.projectenum;

import com.bsoft.assistant.common.CommonDAO;
import org.springframework.context.ApplicationContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by
 * tuz
 * on 2021/12/15.
 */
public interface IResourceEnum<Code, Text> {
    Map<String, Object> cacheOneOptionEnums = new HashMap<>();
    String scanEnumBasePackage = "com.bsoft.assistant.common.projectenum";
    String CODE = "code";
    String TEXT = "text";


    default Code getCode(){
        return null;
    }

    default Text getText(){
        return null;
    }

    default Map<String, Object> transforResourceItem() {
        Map<String, Object> result = new HashMap<>();
        result.put(CODE, this.getCode());
        result.put(TEXT, this.getText());
        return result;
    }

    static List<?> loadData(ApplicationContext applicationContext, String datasourceId, String sql, Object[] param) {
        return applicationContext.getBean(CommonDAO.class).execSqlQuery(datasourceId, sql, param);
    }
}
