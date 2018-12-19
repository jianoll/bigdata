package com.giggs;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.util.Collector;

import java.util.Properties;

public class KafkaDemo {
    //  .\flink.bat run  -jar D:\workspace\bigdata\flink\target\flink008.jar
    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setGlobalJobParameters(params);
//        env.enableCheckpointing(1000);

        Properties properties = new Properties();
        //一直没有在kafka消息里面看到连接请求的日志，不知道为什么，flink控制台也UI页面没有错误消息，一直在running状态,但是结果已经写入到redis中了
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zookeeper.connect", "127.0.0.1:2181");
        properties.setProperty("group.id", "flink001");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
//        new FlinkKafkaConsumer010();
//        String[] lines = {"this is a redis demo","i am a redis test"};
//        FlinkKafkaConsumer09<String> kafkaSource = new FlinkKafkaConsumer09<String>("flink_demo", new SimpleStringSchema(), properties);
        FlinkKafkaConsumer011<String> kafkaSource = new FlinkKafkaConsumer011<String>("flink_demo", new SimpleStringSchema(), properties);
        kafkaSource.setStartFromEarliest();
        DataStream<String> stream = env.addSource(kafkaSource);
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
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("127.0.0.1").setPort(6379).build();
        //实例化RedisSink，并通过flink的addSink的方式将flink计算的结果插入到redis
        counts.addSink(new RedisSink<>(conf, new App.RedisExampleMapper()));

        if (params.has("output")) {
            counts.writeAsText(params.get("output"));
        } else {
            System.out.println("Print result to stdout. Use --output to specify output path.");
            counts.print();
        }
        ((SingleOutputStreamOperator<Tuple2<String, Integer>>) counts).setParallelism(1);
        env.execute("flink kafka demo");
    }
}
