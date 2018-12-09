package com.jxjr.kstream;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;

public class SparkSql {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("").setAppName("sqltest");
//        SparkContext context =  SparkContext.getOrCreate(sparkConf);
//        SQLContext sqlContext = new SQLContext(context);
//        sqlContext.createDataFrame();

    }
}
