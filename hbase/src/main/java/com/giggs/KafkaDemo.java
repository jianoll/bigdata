package com.jxjr.demo;

import com.jxjr.kstream.SparkToRedis;
import kafka.serializer.StringDecoder;
import org.apache.kafka.clients.KafkaClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.awt.*;
import java.util.*;

import static org.apache.flume.source.MultiportSyslogTCPSource.logger;

public class KafkaDemo {
    protected static Logger logger = LoggerFactory.getLogger(SparkToRedis.class);

    public static void main(String[] args) throws InterruptedException {
        //kafkad代码
//        Properties properties = new Properties();
        String topic = "flumetest2";
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("acks", "1");
        properties.put("group.id", "test02");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
//        properties.put("topic.metadata.refresh.interval.ms","-1");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        ArrayList topics = new ArrayList();
        topics.add(topic);
        TopicPartition partition = new TopicPartition(topic,0);
        ArrayList partitions = new ArrayList();
        partitions.add(partition);
//        try{
//            Map existTopics = kafkaConsumer.listTopics();
//            existTopics.remove("__consumer_offsets");
//            System.out.println(existTopics.toString());
//            //kafkaConsumer.subscribe(topics);
//            kafkaConsumer.assign(partitions);
//            System.out.println(kafkaConsumer.position(partition));
//            while (true) {
//                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
//                for (ConsumerRecord<String, String> record : records) {
//                    System.out.printf("offset = %d, value = %s", record.offset(), record.value());
//                }
//
//            }
//        }catch (Exception e){
         while (true){
             try{
                 //kafkaConsumer.subscribe(topics);
                 kafkaConsumer.assign(partitions);
                 while (true) {
                     ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                     for (ConsumerRecord<String, String> record : records) {
                         System.out.printf("offset = %d, value = %s", record.offset(), record.value());
                     }
                 }
             }catch (Exception ex){
                 System.out.println("conncet failed, retrying.....");
             }
             Thread.sleep(500);
         }
//        }


    }
}
