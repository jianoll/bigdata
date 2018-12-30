package com.giggs;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

public class WordCount {
    public static void main(String[] args) {
        Properties properties = new Properties();//streams的配置参数
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka_stream_demo");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> source = builder.stream("kafka_stream01");//使用默认的拓扑从myjavatopic中创建源
        KTable<String,Long> counts=source.flatMapValues(s-> Arrays.asList(s.toLowerCase(Locale.getDefault()).split(" "))).groupBy((s1,s2)->s2).count();
        counts.toStream().to("word_count_demo", Produced.with(Serdes.String(), Serdes.Long()));
        counts.toStream().through("kafka_streams_result");
        Topology topology = builder.build();
//        topology.addSink()
        System.out.println(topology.describe());

//        KafkaStreams streams = new KafkaStreams(topology, properties);
//        KTable<String,Long> counts=source.flatMapValues(new ValueMapper<String, Iterable<String>>() {
//            @Override
//            public Iterable<String> apply(String s) {
//                return Arrays.asList(s.toLowerCase(Locale.getDefault()).split(" "));
//            }
//        }).groupBy((s1,s2)->s2).count();
    }
}
