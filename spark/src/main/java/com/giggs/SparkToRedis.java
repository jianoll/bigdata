package com.giggs;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.kafka.*;
import org.apache.spark.streaming.kafka.DirectKafkaInputDStream;
//import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.kafka.HasOffsetRanges;
import org.apache.spark.streaming.kafka.OffsetRange;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.spark.streaming.kafka.KafkaUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import scala.Tuple1;
import scala.Tuple2;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.spark.streaming.kafka.KafkaUtils;

import org.apache.kafka.common.serialization.StringSerializer;
public class SparkToRedis {
    protected static Logger logger = LoggerFactory.getLogger(SparkToRedis.class);



    public static void main(String[] args) throws InterruptedException {
        //kafkad代码
//        Properties properties = new Properties();
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("bootstrap.servers","172.17.0.6:9092");
        properties.put("group.id", "test023");
        properties.put("auto.offset.reset", "largest");
//        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        SparkConf config = new SparkConf();
        config.setMaster("spark://0.0.0.0:7077").setAppName("logTest02");
//        SparkContext context = new SparkContext(config);
        HashSet topics = new HashSet();
        topics.add("flumetest2");
        StorageLevel level = new StorageLevel();

//        StreamingContext streamingContext = new StreamingContext(config,Duration.apply(1));
        JavaStreamingContext jssc = new JavaStreamingContext(config,Durations.seconds(5));
//        JavaStreamingContext streamingContext = new JavaStreamingContext(config, new Duration(2000));
        JavaPairInputDStream<String, String> kafkaStream= KafkaUtils.createDirectStream(jssc,String.class,String.class,StringDecoder.class,StringDecoder.class,properties,topics);
//        KafkaUtils.createStream(streamingContext,"flumetest2","test02", (scala.collection.immutable.Map<String, Object>) properties,level);

        //lambda写法简洁很多,而且不用考虑函数类型问题
//        JavaDStream splited = kafkaStream.flatMap(record ->{
//            return Arrays.asList(record._2.split("|")).iterator();
//        });
//        AtomicReference<OffsetRange[]> offsetRanges = new AtomicReference<>();
//        kafkaStream.transformToPair(rdd -> {
//            OffsetRange[] offsets = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
//            offsetRanges.set(offsets);
//            return rdd;
//        }).mapToPair(line ->{
//            String [] recordBody = line._2.split("\\|@\\|");
//            return  new Tuple2<>(recordBody[0],recordBody[2]);
//        }).foreachRDD(rdd -> {
//            rdd.foreachPartition(( partitionOfRecords) -> {
//
//                RedisPool pool = new RedisPool("172.17.0.7",6379,10);
//                Jedis redis = pool.getClient();
//                partitionOfRecords.forEachRemaining(record ->{
//                    redis.incrBy(record._1, Long.parseLong(record._2));
//                    redis.set(record.toString(),"record type");
//                    redis.set(record._1,record._2);
//                    System.out.println(record);
//                });
//            });
//        });
        AtomicReference<OffsetRange[]> offsetRanges = new AtomicReference<>();
        kafkaStream.transformToPair(rdd -> {
            OffsetRange[] offsets = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
            offsetRanges.set(offsets);
            return rdd;
        }).mapToPair(line ->{
            String [] recordBody = line._2.split("\\|@\\|");
            return  new Tuple2<>(recordBody[0],recordBody[2]);
        }).reduceByKey(
                (a,b) -> String.valueOf(convert(a)+convert(b))
        ).foreachRDD(rdd -> {
            rdd.foreachPartition(( partitionOfRecords) -> {

//                RedisPool pool = new RedisPool("172.17.0.7",6379,10);
                RedisPool pool = new RedisPool("172.17.0.7:9002");
                Jedis redis = pool.getClient();
                partitionOfRecords.forEachRemaining(record ->{
                    redis.incrBy(record._1, Long.parseLong(record._2));
                    System.out.println(record);
                });
            });
        });
        jssc.start();
        jssc.awaitTermination();
        logger.debug("start job success");
        }

    public static int convert(String s){
        double x = Double.valueOf(s);
        int a = (int) x;
        return a;
    }
}
