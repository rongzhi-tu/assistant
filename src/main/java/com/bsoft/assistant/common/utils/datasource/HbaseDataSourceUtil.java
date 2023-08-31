package com.bsoft.assistant.common.utils.datasource;

import java.io.IOException;


/***
 * 使用Java访问HBase
 * @author zky
 *
 */
public class HbaseDataSourceUtil {

    /*private static HbaseDataSourceUtil INSTANCE = null;

    private static Configuration conf = null;
    //配置文件
    private static Admin admin = null;
    //管理员
    public static Connection conn = null;
    //连接


    private static String  ip;
    private static String  port;

    *//**
     * 初始化配置
     *//*
    private HbaseDataSourceUtil() {

        try {
            if (conf == null) {
                // 必备条件之一
                conf = HBaseConfiguration.create();
                //创建一个配置
                conf.set("hbase.zookeeper.quorum", ip);
                //设置zookeeper的结点主机名
                conf.set("hbase.zookeeper.property.clientPort", port);
                //设置zookeepe端口号
                conf.set("hbase.zookeeper.property.maxclientcnxns", "300");
                conf.set("hbase.ipc.client.socket.timeout.connect", "1000");
                conf.set("zookeeper.session.timeout", "500");
                conf.set("hbase.regionserver.handler.count", "500");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized HbaseDataSourceUtil getInstance(){
        if(INSTANCE == null){
            INSTANCE = new HbaseDataSourceUtil();

            return INSTANCE;
        }
        return INSTANCE;
    }

    *//**
     * 获取conn连接
     *
     * @return
     *//*
    private Connection getConnection() {
        if (conn == null || conn.isClosed()) {
            try {
                conn = ConnectionFactory.createConnection(conf); //配置连接信息

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }


    private void closeConnection() {
        if (admin != null) {
            try {
                admin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    *//**
     * 获取连接
     *
     * @return
     *//*
    public static Connection getHbaseConnInit() {

        return INSTANCE.getConnection();
    }

    *//**
     * 关闭连接
     *//*
    public static void closeHbaseConn() {

        INSTANCE.closeConnection();
    }


    *//**
     * 初始化连接
     *
     * @throws IOException
     *//*
    public static void init() throws IOException {
        // 必备条件之一
        conf = HBaseConfiguration.create();
        //创建一个配置
        conf.set("hbase.zookeeper.quorum", "10.32.3.17");
        //设置zookeeper的结点主机名
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        //设置zookeepe端口号
        conf.set("hbase.zookeeper.property.maxclientcnxns", "300");
        conf.set("hbase.ipc.client.socket.timeout.connect", "1000");
        conf.set("zookeeper.session.timeout", "500");
        conf.set("hbase.regionserver.handler.count", "500");

        conn = ConnectionFactory.createConnection(conf);
        //使用配置创建一个HBase连接
        admin = conn.getAdmin();
        //得到管理器
    }


    public static List<String> getAllTableNames()  {
        List<String> result = new ArrayList<>();
        getHbaseConnInit();
        try {
            admin = conn.getAdmin();
            TableName[] tableNames = admin.listTableNames();
            for (TableName tableName : tableNames) {
                result.add(tableName.getNameAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            destroy();
        }

        return result;
    }

    *//***
     * 建表
     * @param tableName 表名
     * @param families    列簇
     * @throws IOException
     *//*
    public static void createTable(String tableName, String[] families) throws IOException {
        getHbaseConnInit();
        TableName tn = TableName.valueOf(tableName);
        admin=conn.getAdmin();
        //初始化
        if (admin.tableExists(tn))
        //如果表已经存在
        {
            System.out.println(tableName + "已存在");
        } else
        //如果表不存在
        {
            TableDescriptorBuilder tbd = TableDescriptorBuilder.newBuilder(tn);
            //使用表名创建一个表描述器HTableDescriptor对象
            for (String family : families) {
                ColumnFamilyDescriptor cfd = ColumnFamilyDescriptorBuilder.of(family);
                tbd.setColumnFamily(cfd);
                //添加列族
            }
            admin.createTable(tbd.build());
            //创建表
            System.out.println("Table created");
        }
    }

    *//**
     * 新增列簇
     *
     * @param tableName 表名
     * @param family    列族名
     *//*
    public static void addFamily(String tableName, String family) {
        try {
            getHbaseConnInit();
            admin=conn.getAdmin();
            TableName tn = TableName.valueOf(tableName);
            if(!admin.tableExists(tn)){

                System.out.println("table "+ tableName +" not exist !");

                return;
            }

            ColumnFamilyDescriptor desc = ColumnFamilyDescriptorBuilder.of(family);
            admin.addColumnFamily(tn,desc);

            System.out.println("add column family " + family );
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            destroy();
        }
    }

    *//**
     * 查询表信息
     *
     * @param tableName
     *//*
    public static void query(String tableName) {
        HTable hTable = null;
        ResultScanner scann = null;
        try {
            getHbaseConnInit();
            hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
            scann = hTable.getScanner(new Scan());
            for (Result rs : scann) {
                System.out.println("RowKey为：" + new String(rs.getRow()));
                // 按cell进行循环
                for (Cell cell : rs.rawCells()) {
                    System.out.println("列簇为：" + new String(CellUtil.cloneFamily(cell)));
                    System.out.println("列修饰符为：" + new String(CellUtil.cloneQualifier(cell)));
                    System.out.println("值为：" + new String(CellUtil.cloneValue(cell)));
                }
                System.out.println("=============================================");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scann != null) {
                scann.close();
            }
            if (hTable != null) {
                try {
                    hTable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            destroy();
        }
    }

    public static ResultScanner query2(String tableName) {
        HTable hTable = null;
        ResultScanner scann = null;
        try {
            init();
            hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
            scann = hTable.getScanner(new Scan());
            for (Result rs : scann) {
                System.out.println("RowKey为：" + new String(rs.getRow()));
                // 按cell进行循环
                for (Cell cell : rs.rawCells()) {
                    System.out.println("列簇为：" + new String(CellUtil.cloneFamily(cell)));
                    System.out.println("列修饰符为：" + new String(CellUtil.cloneQualifier(cell)));
                    System.out.println("值为：" + new String(CellUtil.cloneValue(cell)));
                }
                System.out.println("=============================================");
            }
            return scann;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scann != null) {
                scann.close();
            }
            if (hTable != null) {
                try {
                    hTable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            destroy();
        }
        return null;
    }

    *//**
     * 根据rowkey查询单行
     *
     * @param key
     * @param tableName
     *//*
    public static void queryByRowKey(String key, String tableName) {
        HTable hTable = null;
        try {
            init();
            hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(key));
            get.setMaxVersions(3);  // will return last 3 versions of row
            Result rs = hTable.get(get);
            System.out.println(tableName + "表RowKey为" + key + "的行数据如下：");
            for (Cell cell : rs.rawCells()) {
                //System.out.println("key为：" + new String(CellUtil.cloneRow(cell)));
                System.out.println("\t列簇为：" + new String(CellUtil.cloneFamily(cell)));
                System.out.println("\t列标识符为：" + new String(CellUtil.cloneQualifier(cell)));
                System.out.println("\t值为：" + new String(CellUtil.cloneValue(cell)));
                System.out.println("\t----------------------------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (hTable != null) {
                try {
                    hTable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            destroy();
        }
    }

    *//**
     * 插入单行单列簇单列修饰符数据
     *
     * @param tableName
     * @param key
     * @param family
     * @param col
     * @param val
     *//*
    public static void addOneRecord(String tableName, String key, String family, String col, String val) {
        HTable hTable = null;
        try {
            init();
            hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
            Put p = new Put(Bytes.toBytes(key));
            p.addColumn(Bytes.toBytes(family), Bytes.toBytes(col), Bytes.toBytes(val));
            if (p.isEmpty()) {
                System.out.println("数据插入异常，请确认数据完整性，稍候重试");
            } else {
                hTable.put(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (hTable != null) {
                try {
                    hTable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            destroy();
        }
    }

    *//**
     * 插入单行单列簇多列修饰符数据
     *
     * @param tableName
     * @param key
     * @param family
     *//*
    public static void addMoreRecord(String tableName, String key, String family, Map<String, String> colVal) {
        HTable hTable = null;
        try {
            init();
            hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
            Put p = new Put(Bytes.toBytes(key));
            for (String col : colVal.keySet()) {
                String val = colVal.get(col);
                if (StringUtils.isNotBlank(val)) {
                    p.addColumn(Bytes.toBytes(family), Bytes.toBytes(col), Bytes.toBytes(val));
                } else {
                    System.out.println("列值为空，请确认数据完整性");
                }
            }
            // 当put对象没有成功插入数据时，此时调用hTable.put(p)方法会报错：java.lang.IllegalArgumentException:No
            // columns to insert
            if (p.isEmpty()) {
                System.out.println("数据插入异常，请确认数据完整性，稍候重试");
            } else {
                hTable.put(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (hTable != null) {
                try {
                    hTable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            destroy();
        }
    }

    *//**
     * 删除指定名称的列簇
     *
     * @param family
     * @param tableName
     *//*
    public static void deleteFamily(String family, String tableName) {
        try {
            init();
            admin.deleteColumn(TableName.valueOf(tableName), Bytes.toBytes(family));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            destroy();
        }
    }

    *//**
     * 删除指定行
     *
     * @param key
     * @param tableName
     *//*
    public static void deleteRow(String key, String tableName) {
        HTable hTable = null;
        try {
            init();
            hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
            hTable.delete(new Delete(Bytes.toBytes(key)));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (hTable != null) {
                try {
                    hTable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            destroy();
        }
    }

    *//**
     * 删除指定表名
     *
     * @param tableName
     *//*
    public static void deleteTable(String tableName) {
        try {
            init();
            // 在删除一张表前，必须先使其失效
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            destroy();
        }
    }

    // 关闭连接
    public static void destroy() {
        if (admin != null) {
            try {
                admin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException {


//       init();
       getAllTableNames();


//        instance.getConnection();
//        insta

        //1、创建表
        *//*String [] families = {"d"};
        createTable("clientdata_test3", families);*//*

        //2、查询hive
        *//*List<ClientData> cds = ClientDataDao.listByID("b97da515fb26be121d09eab14363d013");
        for(ClientData cd : cds)
		{
			String ymd = cd.getTime().toLocaleString().split(" ")[0];
			Map<String, String> colVal = new HashMap<String, String>();
			colVal.put("s", cd.getScreen());
			colVal.put("m", cd.getModel());
			colVal.put("c", cd.getCountry());
			colVal.put("p", cd.getProvince());
			colVal.put("ci", cd.getCity());
			colVal.put("n", cd.getNetwork());
			colVal.put("t", cd.getTime().toLocaleString());
			addMoreRecord("clientdata_test1",cd.getUserID() + "-" + ymd, "d", colVal);
		}*//*
		
		*//*ClientData cd1 = new ClientData();
		cd1.setCity("shanghai");
		cd1.setCountry("china");
		cd1.setModel("xiaomi 6");
		cd1.setNetwork("wifi");
		cd1.setProvince("shanghai");
		cd1.setScreen("1920*1080");
		cd1.setTime(new Date(2018,3,14,12,01,32));
		cd1.setUserID("chenjie");
		saveClientData3(cd1);
		
		cd1.setNetwork("2G");
		cd1.setTime(new Date(2018,3,14,15,00,00));
		saveClientData3(cd1);
		
		cd1.setNetwork("4G");
		cd1.setTime(new Date(2018,3,14,13,36,11));
		saveClientData3(cd1);
		
		cd1.setNetwork("3G");
		cd1.setTime(new Date(2018,3,14,14,00,21));
		saveClientData3(cd1);*//*


        //3、查询hbase
        //queryByRowKey("chenjie-3918-04-14", "clientdata_test3");

        //4、删除
        //deleteTable("clientdata_test5");
    }*/
}