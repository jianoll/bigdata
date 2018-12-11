package com.giggs;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.flink.util.Collector;
import scala.Tuple2;

import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(1000);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
//        properties.setProperty("zookeeper.connect", "127.0.0.1:2181");
        properties.setProperty("group.id", "flink001");

        FlinkKafkaConsumer09<String> flinkConsumer = new FlinkKafkaConsumer09<String>("flink_demo", new SimpleStringSchema(), properties);

        DataStream<String> stream = env.addSource(flinkConsumer);
        DataStream<Tuple2<String, Integer>> counts = stream.flatMap(new LineSplitter()).keyBy(0).sum(1);
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("127.0.0.1").setPort(6379).build();
        //实例化RedisSink，并通过flink的addSink的方式将flink计算的结果插入到redis
        counts.addSink(new RedisSink<Tuple2<String, Integer>>(conf, new RedisExampleMapper()));
        env.execute("flink kafka demo");
    }
    public static final class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        private static final long serialVersionUID = 1L;

        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            String[] tokens = value.toLowerCase().split("\\W+");
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<String, Integer>(token, 1));
                }
            }
        }
    }
    public static final class RedisExampleMapper implements RedisMapper<Tuple2<String,Integer>> {
        public RedisCommandDescription getCommandDescription() {
            return new RedisCommandDescription(RedisCommand.HSET, "flink");
        }

        public String getKeyFromData(Tuple2<String, Integer> data) {
            return data._1;
        }

        public String getValueFromData(Tuple2<String, Integer> data) {
            return data._2.toString();
        }
    }
}