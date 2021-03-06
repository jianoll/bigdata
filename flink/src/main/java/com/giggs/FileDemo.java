package com.giggs;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Properties;

public class FileDemo {
    //使用命令  .\flink.bat run  -jar D:\workspace\bigdata\flink\target\flink005.jar  --input file:///d:\dataset\wordcount.txt  --output file:///D:\progr
    //am\flink-1.7.0\bin\out.txt 运行
    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(1000);
        DataStream<String> stream=null;
        if (!params.has("input")) {
            stream = env.readTextFile(params.get("input"));
        }else {
            stream = env.readTextFile("file:///d:\\dataset\\wordcount.txt");
        }
        Properties properties = new Properties();
//        properties.setProperty("bootstrap.servers", "localhost:9092");
//        properties.setProperty("group.id", "flink001");
//        String[] lines = {"this is a redis demo","i am a redis test"};

//        DataStream<String> stream = env.fromElements(lines);


        DataStream<Tuple2<String, Integer>> counts = stream.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {

            @Override
            public void flatMap(String s,  Collector<Tuple2<String, Integer>> out) throws Exception {
                for (String word:s.split(" ")){
                    if (word.length() > 0) {
                        out.collect(new Tuple2<>(word, 1));
                    }
                }

            }
        })
                .keyBy(0)
                .sum(1);
        if(params.has("output"))
        counts.writeAsCsv(params.get("output"));
        else {
            counts.print();
        }
        //execute应该要在最后
        env.execute("running read file demo");

    }
}
