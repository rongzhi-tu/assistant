package com.bsoft.assistant.common.utils.datasource;

public class HbaseTest {
    /*private HBaseAdmin admin = null;
    // 定义配置对象HBaseConfiguration
    private static Configuration configuration;
    public HbaseTest() throws Exception {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum","10.32.3.17");  //hbase 服务地址
        configuration.set("hbase.zookeeper.property.clientPort","2181"); //端口号
        admin = new HBaseAdmin(configuration);
    }
    // Hbase获取所有的表信息
    public List getAllTables() {
        List<String> tables = null;
        if (admin != null) {
            try {
                HTableDescriptor[] allTable = admin.listTables();
                if (allTable.length > 0)
                    tables = new ArrayList<String>();
                for (HTableDescriptor hTableDescriptor : allTable) {
                    tables.add(hTableDescriptor.getNameAsString());
                    System.out.println(hTableDescriptor.getNameAsString());
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tables;
    }
    public static void main(String[] args) throws Exception {
        HbaseTest hbaseTest = new HbaseTest();
        hbaseTest.getAllTables();
    }*/
}