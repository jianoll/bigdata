package com.giggs;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
//import scala.Tuple2; 不是这个类，使用这个类会报错Specifying keys via field positions is only valid for tuple data types. Type: GenericType<scala.Tuple2>
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.flink.util.Collector;


import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(1000);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
//        properties.setProperty("zookeeper.connect", "127.0.0.1:2181");
        properties.setProperty("group.id", "flink001");

        FlinkKafkaConsumer09<String> flinkConsumer = new FlinkKafkaConsumer09<String>(
                "flink_demo", new SimpleStringSchema(), properties);

        //自定义数据源
//        DataStream<String> stream = env.addSource(new SourceFunction<String>() {
//            @Override
//            public void run(SourceContext<String> sourceContext) throws Exception {
//
//            }
//
//            @Override
//            public void cancel() {
//
//            }
//        });
        DataStream<String> stream = env.addSource(flinkConsumer);
//        DataStream<Tuple2<String,Integer>> wordNum = stream.flatMap()
        DataStream<Tuple2<String, Integer>> counts = stream.flatMap(new LineSplitter())
                .keyBy(0)
                .sum(1);
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("127.0.0.1").setPort(6379).build();
        //实例化RedisSink，并通过flink的addSink的方式将flink计算的结果插入到redis
        counts.addSink(new RedisSink<Tuple2<String, Integer>>(conf, new RedisExampleMapper()));
        env.execute("flink kafka demo");
        if (params.has("output")) {
            counts.writeAsText(params.get("output"));
        } else {
            System.out.println("Print result to stdout. Use --output to specify output path.");
            counts.print();
        }

    }
    public static final class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
//        private static final long serialVersionUID = 1L;
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            String[] tokens = value.toLowerCase().split("\\W+");
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
//                    out.collect((new Tuple2<String, Integer>(token, 1)));
                }
            }
        }
    }
    public static final class RedisExampleMapper implements RedisMapper<Tuple2<String,Integer>> {
        public RedisCommandDescription getCommandDescription() {
//            return new RedisCommandDescription(RedisCommand.SET);//以单独键值对写入
            return new RedisCommandDescription(RedisCommand.HSET, "flink");//以hset写入redis
        }

        public String getKeyFromData(Tuple2<String, Integer> data) {
            return data.f0;
        }

        public String getValueFromData(Tuple2<String, Integer> data) {
            return data.f1.toString();
        }
    }
}