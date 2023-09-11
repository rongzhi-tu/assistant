package com.bsoft.assistant.common;

import com.alibaba.druid.pool.DruidDataSource;
import com.bsoft.assistant.utils.DBUtils;
import ctd.account.UserRoleToken;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

/**
 * Created by
 * tuz
 * on 2021/11/30.
 * 项目共用的基础数据获取逻辑
 */
//@Repository
public class CommonDAO<Entity> implements ApplicationContextAware {



    private ApplicationContext applicationContext = null;
    private static Logger logger = LoggerFactory.getLogger(CommonDAO.class);
    protected final String DEFAULT_DATASOURCE = "dataSource";
    @Autowired
    private SessionFactory mySessionFactory;
    public static final String DEFAULT_CODE ="0.0.0.0";
    public static Boolean isDefaultCode(String code){
        return DEFAULT_CODE.equals(code);
    }

    public List<Map<String, Object>> execSqlQuery(String sql, Object... params){
        return this.execSqlQuery(DEFAULT_DATASOURCE,sql,params);
    }
    public List<Map<String, Object>> execSqlQuery(StringBuilder sqlStringBuilder, Object... params){
        return this.execSqlQuery(DEFAULT_DATASOURCE,sqlStringBuilder.toString(),params);
    }
    /**
     * 原生状态的sql语句使用框架的StatelessSession进行操作查询获取数据
     *
     * @param sql     : sql语句
     * @param params ：参数列表
     * @return : List<Map<String,Object>>
     */
    public List<Map<String, Object>> execSqlQuery(String datasourceId, String sql, Object... params) {
        List<Map<String, Object>> result = new LinkedList<>();
        Connection connection = null;
        PreparedStatement pst = null;
        try {
            DruidDataSource dataSource = (DruidDataSource)this.applicationContext.getBean(datasourceId);
            connection = dataSource.getConnection();
            pst = connection.prepareStatement(sql);
            if (params != null){
                for (int i = 1; i <= params.length; i++) {
                    if (params[i-1] instanceof Array){
                        pst.setArray(i, (Array) params[i-1]);
                    }else{
                        pst.setObject(i, params[i-1]);
                    }
                }
            }
            ResultSet resultSet = pst.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>(16);
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    map.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                result.add(map);
            }
        } catch (Exception e) {
            logger.error("BaseDAO execSqlQuery : ", e);
        }finally {
            DBUtils.closeConnection(pst,connection,null,"BaseDAO execSqlQuery : ");
        }
        return result;
    }

    public boolean checkTokenExist(){
        return UserRoleToken.getCurrent() != null;
    }
    protected String getCurrentUserId() {
        return UserRoleToken.getCurrent().getUserId();
    }

    protected String getCurrentDeptId() {
        return UserRoleToken.getCurrent().getDeptId();
    }

    protected String getCurrentOrgId() {
        return UserRoleToken.getCurrent().getOrgId();
    }
    public Class<Entity> getEntityClass(){
        Type superclass = this.getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType){
            ParameterizedType type = (ParameterizedType)superclass;
            return (Class<Entity>)type.getActualTypeArguments()[0];
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
