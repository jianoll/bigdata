package com.jxjr.demo;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class HbaseDemo {

        private static Logger logger = LoggerFactory.getLogger("helloLogger");
        public static Configuration configuration;
        public static Connection connection;
        public static Admin admin;

        public static void main(String[] args) throws IOException {

            System.out.println(LoggerFactory.getILoggerFactory().getClass());
            Enumeration manager  = LogManager.getCurrentLoggers();
            while (manager.hasMoreElements()){
                System.out.println(manager.nextElement().getClass());
            }

            //createTable("t2", new String[] { "cf1", "cf2" });
            listTables();


            createTable("t_product_list_json",new String[] {"nodeid","start","review_num","product_name","url"});
            listTables();
            /*
             * insterRow("t2", "rw1", "cf1", "q1", "val1"); getData("t2", "rw1",
             * "cf1", "q1"); scanData("t2", "rw1", "rw2");
             * deleRow("t2","rw1","cf1","q1"); deleteTable("t2");
             */
        }

        // 初始化链接
        public static void init() {
            System.setProperty("hadoop.home.dir", "D:\\program\\hadoop-common-2.2.0-bin");
            configuration = HBaseConfiguration.create();
            /*
             * configuration.set("hbase.zookeeper.quorum",
             * "10.10.3.181,10.10.3.182,10.10.3.183");
             * configuration.set("hbase.zookeeper.property.clientPort","2181");
             * configuration.set("zookeeper.znode.parent","/hbase");
             */
            configuration.set("hbase.zookeeper.property.clientPort", "2181");
            configuration.set("hbase.zookeeper.quorum", "127.0.0.1");
            configuration.set("hbase.master", "127.0.0.1:16000");
            File workaround = new File(".");
            System.getProperties().put("hadoop.home.dir",
                    workaround.getAbsolutePath());
            //new File("D:\\program\\hadoop-common-2.2.0-bin\\bin").mkdirs();
            try {
                new File("./winutils.exe").createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                connection = ConnectionFactory.createConnection(configuration);
                admin = connection.getAdmin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 关闭连接
        public static void close() {
            try {
                if (null != admin)
                    admin.close();
                if (null != connection)
                    connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // 建表
        public static void createTable(String tableNmae, String[] cols) throws IOException {

            init();
            TableName tableName = TableName.valueOf(tableNmae);

            if (admin.tableExists(tableName)) {
                System.out.println("talbe is exists!");
            } else {


                HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
                for (String col : cols) {
                    System.out.println(col);
                    HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(col);
                    hTableDescriptor.addFamily(hColumnDescriptor);
                }
                admin.createTable(hTableDescriptor);
            }
            close();
        }

        // 删表
        public static void deleteTable(String tableName) throws IOException {
            init();
            TableName tn = TableName.valueOf(tableName);
            if (admin.tableExists(tn)) {
                admin.disableTable(tn);
                admin.deleteTable(tn);
            }
            close();
        }

        // 查看已有表
        public static void listTables() throws IOException {
            init();
            HTableDescriptor hTableDescriptors[] = admin.listTables();
            for (HTableDescriptor hTableDescriptor : hTableDescriptors) {
                System.out.println(hTableDescriptor.getNameAsString());
            }
            close();
        }

        // 插入数据
        public static void insterRow(String tableName, String rowkey, String colFamily, String col, String val)
                throws IOException {
            init();
            Table table = connection.getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowkey));
            put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col), Bytes.toBytes(val));
            table.put(put);

            // 批量插入
            /*
             * List<Put> putList = new ArrayList<Put>(); puts.add(put);
             * table.put(putList);
             */
            table.close();
            close();
        }

        // 删除数据
        public static void deleRow(String tableName, String rowkey, String colFamily, String col) throws IOException {
            init();
            Table table = connection.getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(Bytes.toBytes(rowkey));
            // 删除指定列族
            // delete.addFamily(Bytes.toBytes(colFamily));
            // 删除指定列
            // delete.addColumn(Bytes.toBytes(colFamily),Bytes.toBytes(col));
            table.delete(delete);
            // 批量删除
            /*
             * List<Delete> deleteList = new ArrayList<Delete>();
             * deleteList.add(delete); table.delete(deleteList);
             */
            table.close();
            close();
        }

        // 根据rowkey查找数据
        public static void getData(String tableName, String rowkey, String colFamily, String col) throws IOException {
            init();
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowkey));
            // 获取指定列族数据
            // get.addFamily(Bytes.toBytes(colFamily));
            // 获取指定列数据
            // get.addColumn(Bytes.toBytes(colFamily),Bytes.toBytes(col));
            Result result = table.get(get);

            showCell(result);
            table.close();
            close();
        }

        // 格式化输出
        public static void showCell(Result result) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                System.out.println("RowName:" + new String(CellUtil.cloneRow(cell)) + " ");
                System.out.println("Timetamp:" + cell.getTimestamp() + " ");
                System.out.println("column Family:" + new String(CellUtil.cloneFamily(cell)) + " ");
                System.out.println("row Name:" + new String(CellUtil.cloneQualifier(cell)) + " ");
                System.out.println("value:" + new String(CellUtil.cloneValue(cell)) + " ");
            }
        }

        // 批量查找数据
        public static void scanData(String tableName, String startRow, String stopRow) throws IOException {
            init();
            Table table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            // scan.setStartRow(Bytes.toBytes(startRow));
            // scan.setStopRow(Bytes.toBytes(stopRow));
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                showCell(result);
            }
            table.close();
            close();
        }

    }