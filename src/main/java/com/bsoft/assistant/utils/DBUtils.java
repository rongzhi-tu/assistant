package com.bsoft.assistant.utils;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import ctd.persistence.bean.QueryResult;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.dialect.OracleSqlDialect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Ycl
 * @Description:
 * @Date Created in 2022/10/24  17:44.
 */
public class DBUtils {
    //private static final Logger logger = LoggerFactory.getLogger(DBUtils.class);
    private static String REPEAT_SQL_WHERE = "";
    private static String BLOB_APPEND = "DECLARE  " +
            " loc_c CLOB;  " +
            " buf_c VARCHAR2(6198);  " +
            " loc_b BLOB;  " +
            " buf_b RAW(6198);  " +
            " loc_nc NCLOB;  " +
            " buf_nc NVARCHAR2(6198);  " +
            " e_len NUMBER;  " +
            "BEGIN " +
            " select %s into loc_b from %s where %s  for update; " +
            " " +
            " buf_b := HEXTORAW('%s');  " +
            "  dbms_lob.write(loc_b, %s, %s, buf_b); " +
            "END;";

    public static void close(Connection connection) throws Exception {
        if (null != connection) {
            connection.close();
        }
    }

    public static List<Map<String, Object>> queryList(Connection connection, String queryTablesSql, Map<String, Object> params) throws Exception {
        PreparedStatement preparedStatement = connection.prepareStatement(queryTablesSql);
        if (null != params && params.size() > 0) {
            Set<String> keys = params.keySet();
            Iterator<String> iterator = keys.iterator();
            int paramIndex = 0;
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = params.get(key);
                preparedStatement.setObject(paramIndex, value);
                paramIndex++;
            }
        }
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int colCount = metaData.getColumnCount();
        List<Map<String, Object>> result = new CopyOnWriteArrayList<Map<String, Object>>();
        while (resultSet.next()) {
            Map<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
            for (int i = 1; i <= colCount; i++) {
                String colName = metaData.getColumnName(i);
                Object colValue = resultSet.getObject(colName);
                resultMap.put("label", colValue);
                resultMap.put("value", colValue);
            }
            result.add(resultMap);
        }
        resultSet.close();
        preparedStatement.close();
        close(connection);
        return result;
    }

    public static void executeSql(Connection connection, String sql) throws Exception {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.execute();
        } catch (Exception e) {
            throw e;
        } finally {
            DBUtils.closeConnection(preparedStatement, connection, null, "executeSql");
        }
    }

    public static boolean isNotEmptyAndNull(String str) {
        if (StringUtils.isNotEmpty(str) && !str.startsWith("null")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * SQL拼接方法：源库日志SQL，添加OPERATION-操作类型 、
     * LAST_MODIFY_TIME-源库该记录最后修改时间 、LDR_TIME-同步到目标库的时间
     *
     * @param sql
     * @param contents
     * @return
     * @throws Exception
     */
    public static Map<String, Object> appendSql(String sql, String[] contents) throws Exception {
        //insert into "RHIP403"."AAA_CLONG002"("ID","NAME","TEXT1") values ('001198','001198线程001','aaaaaaaaaaaaaa1198')
        //update "RHIP403"."AAA_CLONG002" set name='1200' , TEXT1='12001200' where id = '001200';
        Map<String, Object> result = new HashMap<>();
        String StrNew = "";
        String extendSql = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (isNotEmptyAndNull(contents[3])) {
            if (contents[3].equals("1")) {
                String strTemp = sql.replace(") values (", ",  OPERATION  ,  LAST_MODIFY_TIME  ,  LDR_TIME  ) values (");
                StrNew = strTemp.substring(0, strTemp.length() - 1) + ",?,?,?)";
                StrNew = StrNew.replace("?,?,?)", "") + "'I',to_date('" +
                        contents[2].replace(".0", "") + "' , 'yyyy-mm-dd hh24:mi:ss')"
                        + ",to_date('" + sdf.format(new Date()) + "' , 'yyyy-mm-dd hh24:mi:ss'))";
            } else if (contents[3].equals("3")) {
                StrNew = sql.replace(" where ", " ,  OPERATION  =?,   LAST_MODIFY_TIME  =? where ");
                StrNew = StrNew.replace(",  OPERATION  =?", ",  OPERATION  ='U'")
                        .replace(",   LAST_MODIFY_TIME  =?", ",   LAST_MODIFY_TIME  =to_date('" +
                                contents[2].replace(".0", "") + "' , 'yyyy-mm-dd hh24:mi:ss')");
            } else if (contents[3].equals("10")) {
                if (isNotEmptyAndNull(contents[4])) {
                    if (sql.split(contents[4]).length == 2 && sql.split(contents[4])[1].split("' for update;").length == 2) {
                        String strTemp = sql.split(contents[4])[1].split("' for update;")[0];
                        if (!REPEAT_SQL_WHERE.equals(strTemp + contents[2] + contents[4])) {
                            extendSql = "update   " + contents[4].replace("where", "") +
                                    " set OPERATION='U', LAST_MODIFY_TIME=to_date('" + contents[2].replace(".0", "")
                                    + "' , 'yyyy-mm-dd hh24:mi:ss') where " + strTemp + "'";
                            REPEAT_SQL_WHERE = strTemp + contents[2] + contents[4];
                        }
                    }
                }
                StrNew = sql;
            } else {
                StrNew = sql;
            }
        } else {
            StrNew = sql;
        }

        result.put("sql", StrNew);
        result.put("extendSql", extendSql);
        result.put("operation_code", isNotEmptyAndNull(contents[3]) ? contents[3] : "");
        return result;
    }

    public static List<String> splitString(String str, int length, String logId) {
        Logger logger = LoggerFactory.getLogger(DBUtils.class);
        List<String> strs = new ArrayList<>();
        try {
            String FieldName = str.split("set")[1].split("=")[0];
            String tableName = str.split("set")[0].replace("update", "");
            String whereString = str.split("\\) where ")[1];
            String content = str.split("HEXTORAW\\('")[1].split("'\\)")[0];
            if (content.length() >= 3999) {
                logger.info("blob非存储过程语句拼接..." + str);
                if (content.length() <= length) {
                    String sql = String.format(BLOB_APPEND, FieldName, tableName, whereString, content, content.length() / 2, 1);
                    strs.add(sql + "##assistant##" + logId);
                } else {
                    int n = (content.length() + length - 1) / length; //获取整个字符串可以被切割成字符子串的个数
                    int count = 1;
                    for (int i = 0; i < n; i++) {
                        String sql = "";
                        if (i < (n - 1)) {
                            sql = String.format(BLOB_APPEND, FieldName, tableName, whereString, content.substring(i * length, (i + 1) * length), 2048, count);
                            count += 2048;
                            strs.add(sql + "##assistant##" + logId);
                        } else {
                            int len1 = content.substring(i * length).length() / 2;
                            sql = String.format(BLOB_APPEND, FieldName, tableName, whereString, content.substring(i * length), len1, count);
                            strs.add(sql + "##assistant##" + logId);
                        }
                    }
                }
            } else {
                strs.add(str + "##assistant##" + logId);
            }
        } catch (Exception e) {
            logger.info("====blob非存储过程截取错误=====" + str);
        }
        return strs;
    }

    public static void executeAppendSql(Connection connection, List<String> sqls) throws Exception {
        Logger logger = LoggerFactory.getLogger(DBUtils.class);
        Statement statement = connection.createStatement();
        connection.setAutoCommit(false);
        logger.info("==目标库批量执行SQL数量==" + sqls.size());
        for (String sql : sqls) {
            logger.info("=====目标库执行SQL======" + sql.split("##logmirror##")[0]);
            statement.addBatch(sql.split("##logmirror##")[0]);
        }
        try {
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("关闭预编译语句发生异常: " + e.getMessage());
            }
            DBUtils.closeConnection(null, connection, null, "executeAppendSql");
        }
    }

    public List<Map<String, Object>> queryListWithOutOrder(String sql, Connection connection) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ResultSet rs = null;
        ResultSetMetaData md = null;
        PreparedStatement ps = null;
        try {
            connection.clearWarnings();
            ps = connection.prepareStatement(sql);
            ps.setFetchSize(1000);
            rs = ps.executeQuery();
            md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            this.getList(list, rs, columnCount, md);
        } finally {
            closeResultSet(rs, ps);
        }
        return list;
    }

    private void closeResultSet(ResultSet rs, PreparedStatement ps) {
        try {
            if (null != rs) {
                rs.close();
            }
            if (null != ps) {
                ps.close();
            }
        } catch (Exception e) {
//            logger.info(e.getMessage());
        }
    }

    private List<Map<String, Object>> getList(List<Map<String, Object>> list, ResultSet rs, int columnCount, ResultSetMetaData md) throws SQLException {
        while (rs.next()) {
            Map<String, Object> rowData = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnLabel(i).toUpperCase(), rs.getObject(i));
            }
            list.add(rowData);
        }
        return list;
    }

    public boolean execute(String sql, Connection connection) throws SQLException {
        boolean isOk = false;
        PreparedStatement state = null;
        try {
            //执行SQL,返回boolean值表示是否包含ResultSet
            state = connection.prepareStatement(sql);
            state.execute();
        } finally {
            closeResultSet(null, state);
        }
        return isOk;
    }

    public <T> List<T> queryListObject(String sql, Connection conn, Class T) throws SQLException {
        List<T> list = new ArrayList<T>();
        BeanListHandler<T> bh = new BeanListHandler<T>(T);
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            list = bh.handle(rs);
        } finally {
            closeResultSet(rs, ps);
        }
        return list;
    }

    public static long queryTableCount(Connection fromCon, String tableName) throws Exception {
        String sumSql = "select count(1) as DATASUM from " + tableName;
        ResultSet rs = null;
        PreparedStatement ps = null;
        long dataSum = 0;
        try {
            ps = fromCon.prepareStatement(sumSql);
            rs = ps.executeQuery();
            if (rs.next()) {
                dataSum = rs.getLong("DATASUM");
            }
        } finally {
            rs.close();
            ps.close();
        }
        return dataSum;
    }

    /**
     * tuz 规范关闭数据库连接
     *
     * @param preparedStatement ： preparedStatement
     * @param connection        ：connection
     * @param session           ：session
     * @param mark              ：日志标识文字
     */
    public static void closeConnection(PreparedStatement preparedStatement, Connection connection, Session session, String mark) {
        Logger logger = LoggerFactory.getLogger(DBUtils.class);
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
            if (session != null) {
                session.close();
            }
            logger.info("[" + mark + "] : 连接关闭成功！");
        } catch (SQLException e) {
            throw new RuntimeException("[" + mark + "]数据库资源关闭失败：" +
                    e.getMessage());
        }
    }

    public static PreparedStatement judgeAndSet(PreparedStatement ppst, String value, int idx, boolean flag) throws SQLException {
        if (StringUtils.isNotEmpty(value) && value.length() > 3700) {
            if (flag) {
                ppst.setString(idx, value.substring(0, 3700));
            } else {
                ppst.setString(idx, value.substring(3700));
            }
        } else {
            if (flag) {
                ppst.setString(idx, StringUtils.isNotEmpty(value) ? value : "");
            } else {
                ppst.setString(idx, "");
            }
        }
        return ppst;
    }

    /****
     * 3. 获取查询数据的条数
     * @param countRs ResultSet
     * @param countField count(1)的别名
     * @return 数据的条数
     * @throws SQLException sql错误
     */
    public static Integer getQueryCountNum(ResultSet countRs, String countField) throws SQLException {
        //表的条数赋值
        int dataCount = 0;
        while (countRs.next()) {
            dataCount = Integer.parseInt(countRs.getString(countField));
        }
        return dataCount;
    }

    /*****
     * 4. 获取查询数据结果集
     * @param rs ResultSet
     * @return 查询结果返回数据内容
     * @throws SQLException sql异常
     */
    public static List<Map<String, Object>> getQueryDataList(ResultSet rs, QueryResult queryResult) throws SQLException {
        Map<String, String> convertMap = new HashMap() {
            {
                put("DATASUBNAME", "dataSubName");
                put("DATASOURCEID", "dataSourceId");
                put("DATATOSOURCEID", "dataToSourceId");
                put("TABLENAME", "tableName");
                put("DATASUBNAME", "dataSubName");
                put("DATASUBNAME", "dataSubName");
                put("RN", "rn");
                put("SOURCESUM", "sourceSum");
                put("TARGETSUM", "targetSum");
                put("DATASUBID", "dataSubId");
            }
        };

        List<Map<String, Object>> list = new ArrayList<>();
        // 2. 获取结果的列数
        int columnCount = rs.getMetaData().getColumnCount();

        // 3. 遍历获取值
        while (rs.next()) {
            Map<String, Object> resultMap = new HashMap<>();
            // 1.1. 获取结果的数据源信息
            ResultSetMetaData metaData = rs.getMetaData();

            // 2. 循环获取 里面的内容
            for (Integer i = 1; i <= columnCount; i++) {
                // 2.1. 剔除 结果集包含 rowNum 之类的字段结果
                if (!"rowNum".equalsIgnoreCase(metaData.getColumnName(i)) && !"rn".equalsIgnoreCase(metaData.getColumnName(i))) {
                    if ("totalNum".equalsIgnoreCase(metaData.getColumnName(i))) {
                        if (queryResult.getTotal() == 0) {
                            queryResult.setTotal(Integer.parseInt(rs.getString(i)));
                        }
                        continue;
                    }
                    resultMap.put(convertMap.get(metaData.getColumnName(i)), rs.getString(i));
                }
            }
            list.add(resultMap);
        }
        // 5. 返回结果
        return list;
    }


    public static void main11(String[] args) throws SqlParseException {
        String sql = "alter table hi_bi_page_component_conf_data add column metric_data_set varchar(100)";
        SqlParser.Config mysqlConfig = SqlParser.config()
                .withParserFactory(SqlDdlParserImpl.FACTORY)
                .withConformance(SqlConformanceEnum.MYSQL_5)
                .withLex(Lex.MYSQL);
        SqlParser parser = SqlParser.create(sql, mysqlConfig);
        SqlNode sqlNode = parser.parseStmt();
//        System.out.println(sqlNode.toSqlString(OracleSqlDialect.DEFAULT));
        System.out.println(sqlNode.toSqlString(OracleSqlDialect.DEFAULT).getSql());
    }

    public static void main(String[] args) {
        int i = 0;
//        for (; i < 10 ; i++){
        String sql = "INSERT INTO `hi_bi_component" + i + "` VALUES ('1', '柱状图', 'componentCategory2', 'echarts', 'echartComponent', 'barChart', '100', '组件列表菜单', 'fa fa-line-chart', '', '', '2022-01-04 15:18:48', null, '2022-01-17 14:59:57', '1', null, '2022-01-17 14:59:57', '', '')";
         sql = "update AAA_CLONG002   set   TEXT1   = 'aaaaaaaaaaaaaa5008' where   ID   = '005008' and   NAME   = '005008线程001' and   EXTEND555   IS NULL and   CREATE_TIME   IS NULL and   UPDATE_TIME   IS NULL and   EXTEND666   IS NULL and   CLONG01   IS NULL and   CLONG02   IS NULL and   CLONG03   IS NULL";
         sql = "SELECT TOP 1000 [ID] " +
                 "      ,[NAME]" +
                 "  FROM [TEST].[dbo].[Table_1]";
        long start = System.currentTimeMillis();
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.sqlserver);
        System.out.println("parse : " + (System.currentTimeMillis() - start));
        String oracleSql = SQLUtils.toSQLString(sqlStatements, DbType.oracle);
        System.out.println("toSql : " + oracleSql);
//        }

    }
}
