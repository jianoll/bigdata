package com.giggs;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.evictors.Evictor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.runtime.operators.windowing.TimestampedValue;
import org.apache.flink.table.api.java.StreamTableEnvironment;


import java.util.Properties;

public class WindowDemo {
    public static void main(String[] args) {
//        StreamExecutionEnvironment env = StreamContextEnvironment.getExecutionEnvironment();
        final ParameterTool params = ParameterTool.fromArgs(args);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zookeeper.connect", "127.0.0.1:2181");
        properties.setProperty("group.id", "flink001");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.getTableEnvironment(env);
        //使用import org.apache.flink.table.api.java.StreamTableEnvironment; 一直报错：
        //[ERROR] /E:/workspace/bigdata/flink/src/main/java/com/giggs/WindowDemo.java:[33,73] 无法访问org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
        //[ERROR] 找不到org.apache.flink.streaming.api.scala.StreamExecutionEnvironment的类文件
        //莫名其妙啊，为什么引入的是java的包，一直报找不到Scala包下面的类呢

        DataStream<String> kafkaSource =  env.addSource(new FlinkKafkaConsumer011<String>("flink_demo", new SimpleStringSchema(), properties));
//        kafkaSource.map(s->new Tuple2(s,1));
//        instanceof
//        tableEnvironment.registerDataStream("test",kafkaSource,'a,'b,'c);

        kafkaSource.keyBy(1).window(TumblingEventTimeWindows.of(Time.seconds(5))).allowedLateness(Time.seconds(1)).trigger(new Trigger<String, TimeWindow>() {
            @Override
            public TriggerResult onElement(String s, long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
                return null;
            }

            @Override
            public TriggerResult onProcessingTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
                return null;
            }

            @Override
            public TriggerResult onEventTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
                return null;
            }

            @Override
            public void clear(TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {

            }
        }).evictor(new Evictor<String, TimeWindow>() {
            @Override
            public void evictBefore(Iterable<TimestampedValue<String>> iterable, int i, TimeWindow timeWindow, EvictorContext evictorContext) {

            }

            @Override
            public void evictAfter(Iterable<TimestampedValue<String>> iterable, int i, TimeWindow timeWindow, EvictorContext evictorContext) {

            }
        });
    }
}
