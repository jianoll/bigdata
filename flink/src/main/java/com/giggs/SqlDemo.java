package com.giggs;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;

//import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment;

public class SqlDemo {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        BatchTableEnvironment tableEnv = BatchTableEnvironment.getTableEnvironment(env);
        //[ERROR] /E:/workspace/bigdata/flink/src/main/java/com/giggs/SqlDemo.java:[22,63]
        // 无法访问org.apache.flink.api.scala.ExecutionEnvironment
        //[ERROR] 找不到org.apache.flink.api.scala.ExecutionEnvironment的类文件
        //该导入的包也不行，最后在maven添加依赖flink-streaming-scala_2.12 解决了这个问题

        //source,这里读取CSV文件，并转换为对应的Class
        //App,Category,Rating,Reviews,Size,Installs,Type,Price,Content Rating,Genres,Last Updated,Current Ver,Android Ver
        DataSet<AppInfo> csvInput = env.readCsvFile("E://data//dataset//googleplaystore.csv")
                .fieldDelimiter("\t")
                .ignoreFirstLine()
//                .types(String.class, String.class, String.class, String.class, String.class, String.class, String.class)
                .pojoType(AppInfo.class,"app","category","rating","reviews","size","installs",
                        "type","price","contentRating","genres","lastUpdated","currentVer","androidVer");

//        TableSource csvSource = new CsvTableSource("/path/to/file");
//        tableEnv.registerTableSource("CsvTable", csvSource);

//        // 创建一个外部catalog
//        ExternalCatalog catalog = new InMemoryExternalCatalog();
//
//// 注册 ExternalCatalog
//        tableEnv.registerExternalCatalog("InMemCatalog", catalog);


        //将DataSet转换为Table
        Table topScore = tableEnv.fromDataSet(csvInput);
        //报错java.lang.NoSuchMethodError: scala.Predef$.refArrayOps ，修改pom添加flink-scala_2.12依赖
        //将topScore注册为一个表
        tableEnv.registerTable("app_detail",topScore);
        Table groupedByCountry = tableEnv.sqlQuery("select category,count(*) as num from app_detail group by category");
        //转换回dataset
        DataSet<Result> result = tableEnv.toDataSet(groupedByCountry,Result.class);

        //将dataset map成tuple输出
        result.map(new MapFunction<Result, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(Result result) throws Exception {
                String category = result.category;
                long num = result.num;
                //这个可能会存在类型转换的错误问题

                return Tuple2.of(category,num);
            }
        }).print();

    }

    /**
     * 源数据的映射类
     */
    public static class AppInfo {
        public String app;
        public String category;
        public String rating;
        public String reviews;
        public String size;
        public String installs;
        public String type;
        public String price;
        public String contentRating;
        public String genres;
        public String lastUpdated;
        public String currentVer;
        public String androidVer;

        public AppInfo() {
            super();
        }
    }

    /**
     * 统计结果对应的类
     */
    public static class Result {
        public String category;
        public long num;

        public Result() {}
    }
}