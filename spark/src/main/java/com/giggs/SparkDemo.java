package com.jxjr.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.streaming.rdd.*;




public class SparkDemo {
    public static void main(String[] args) {
        SparkConf config = new SparkConf();
        config.setAppName("").setMaster("");
        SparkContext context = SparkContext.getOrCreate(config);




    }
}
