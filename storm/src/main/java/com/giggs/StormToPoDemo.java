package com.jxjr.storm;

//import org.apache.storm.StormSubmitter;
//import org.apache.storm.trident.testing.FixedBatchSpout;
//import org.apache.storm.tuple.Fields;
//import org.apache.storm.tuple.Values;
//import java.util.Properties;
//import org.apache.storm.kafka.bolt.KafkaBolt;
//import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
//import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
//import org.apache.storm.kafka.spout.KafkaSpout;
//import org.apache.storm.kafka.BrokerHosts;
//import org.apache.storm.kafka.SpoutConfig;
//import org.apache.storm.kafka.StringScheme;
//import org.apache.storm.kafka.ZkHosts;
//import org.apache.storm.kafka.trident.OpaqueTridentKafkaSpout;
//import org.apache.storm.kafka.trident.TridentKafkaConfig;
//import org.apache.storm.spout.SchemeAsMultiScheme;
//import org.apache.storm.trident.TridentTopology;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;


public class StormToPoDemo {
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        System.setProperty("storm.jar", "/apache-storm-1.2.2/lib/storm-core-1.0.1.jar");
        //1.2之前的storm-kafka代码，已废弃
//        ZkHosts zkHosts = new ZkHosts("localhost:2181");
//        KafkaConfig kafkaConfig = new KafkaConfig(brokerHosts,"storm_demo");
//        BrokerHosts brokerHosts = new ZkHosts("localhost:2181");
//        SpoutConfig spoutConfig = new SpoutConfig(brokerHosts,"storm_demo","/storm_consumer","test001");
//        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
//        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
            //Trident 方式
//        TridentTopology topology = new TridentTopology();
//        BrokerHosts zk = new ZkHosts("localhost");
//        TridentKafkaConfig spoutConf = new TridentKafkaConfig(zk, "test-topic");
//        spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
//        OpaqueTridentKafkaSpout spout = new OpaqueTridentKafkaSpout(spoutConf);

        //storm-kafka-client代码，1.2.2之后用这种方式
//        TopologyBuilder builder = new TopologyBuilder();
//        Builder configBuilder = new Builder("localhost:9092","storm_demo");
//        KafkaSpoutConfig kafkaSpoutConfig = new KafkaSpoutConfig(configBuilder);
//        KafkaSpout kafkaSpout = new KafkaSpout(kafkaSpoutConfig);
//
//        //bolt配置
//        Properties props = new Properties();
//        props.put("bootstrap.servers", "localhost:9092");
//        props.put("acks", "1");
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        KafkaBolt bolt = new KafkaBolt()
//                .withProducerProperties(props)
//                .withTopicSelector(new DefaultTopicSelector("storm_result"))
//                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());
//        builder.setBolt("forwardToKafka", bolt, 8).shuffleGrouping("spout");
//        Config conf = new Config();
//        StormSubmitter.submitTopology("kafkaboltTest", conf, builder.createTopology());
//自定义spout和bolt，然后运行
        TopologyBuilder topobuilder = new TopologyBuilder();
        topobuilder.setSpout("spoutdemo", (IRichSpout) new SpoutDemo());
        topobuilder.setBolt("bolt_demo", (IBasicBolt) new BoltDemo());
        Config conf = new Config();
        if (args == null || args.length == 0) {
            // 本地执行
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("wordcount", conf, topobuilder.createTopology());
        } else {
            conf.setNumWorkers(1);
            try {
                StormSubmitter.submitTopology(args[0], conf, topobuilder.createTopology());
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            }
        }

        //bin/storm jar xxx.jar 主类路径 wordcount
//        Fields fields = new Fields("key", "message");
//        FixedBatchSpout spout = new FixedBatchSpout(fields, 4,
//                new Values("storm", "1"),
//                new Values("trident", "1"),
//                new Values("needs", "1"),
//                new Values("javadoc", "1")
//        );
//        spout.setCycle(true);
//        builder.setSpout("spout", spout, 5);
//        Properties props = new Properties();
//        props.put("bootstrap.servers", "localhost:9092");
//        props.put("acks", "1");
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        KafkaBolt bolt = new KafkaBolt()
//                .withProducerProperties(props)
//                .withTopicSelector(new DefaultTopicSelector("test"))
//                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());
//        builder.setBolt("forwardToKafka", bolt, 8).shuffleGrouping("spout");
//        Config conf = new Config();

//        StormSubmitter.submitTopology("kafkaboltTest", conf, builder.createTopology());//        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);


// --------------------------------------------------------------------------
//        ZkHosts zk = new ZkHosts("","");
//        SpoutConfig spoutConf = new SpoutConfig(zk, "storm_demo",
//                0,//偏移量 offset 的根目录
//                app);//对应一个应用
//
//        List<String> zkServices = new ArrayList<>();
//
//        for(String str : zk.brokerZkStr.split(",")){
//            zkServices.add(str.split(":")[0]);
//        }
//
//        spoutConf.zkServers = zkServices;
//        spoutConf.zkPort = 2181;
//        spoutConf.forceFromStart = false;// true:从头消费  false:从offset处消费
//        spoutConf.socketTimeoutMs = 60 * 1000;
//        spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
//
//        TopologyBuilder builder = new TopologyBuilder();
//        builder.setSpout("spout", new KafkaSpout(spoutConf),4);
//        //builder.setSpout("spout", new TestSpout(),5);
//        builder.setBolt("bolt1", new GetMsgBolt(),4).shuffleGrouping("spout");
//
//        Config config = new Config();
//        config.setDebug(false);
//        config.setNumWorkers(4);
//        if(args.length>0){
//            try {
//                StormSubmitter.submitTopology(args[0], config, builder.createTopology());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }else{
//            LocalCluster cluster = new LocalCluster();
//            cluster.submitTopology("MyTopology", config, builder.createTopology());
//        }


    }
}
