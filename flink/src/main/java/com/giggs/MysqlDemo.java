package com.giggs;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.util.Collector;
import java.util.Properties;



public class MysqlDemo {
    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(1000);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "flink001");
        String[] lines = {"this is a redis demo","i am a redis test"};

        DataStream<String> stream = env.fromElements(lines);

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

        env.execute("flink kafka demo");
        if (params.has("output")) {
            counts.writeAsText(params.get("output"));
        } else {
            System.out.println("Print result to stdout. Use --output to specify output path.");
            counts.print();
        }


    }
}
