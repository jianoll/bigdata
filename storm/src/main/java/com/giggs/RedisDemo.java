package com.jxjr.storm;

import org.apache.storm.redis.bolt.RedisStoreBolt;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;

public class RedisDemo {
    public static void main(String[] args) {
        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
                .setHost("127.0.0.1").setPort(6379).build();
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("random_spout", (IRichSpout) new SpoutDemo());
        RedisStoreMapper storeMapper = new RedisStoreDemo();
        RedisStoreBolt storeBolt = new RedisStoreBolt(poolConfig, storeMapper);
//        RedisState.Factory factory = new RedisState.Factory(poolConfig);


        builder.setBolt("redis_bolt_demo",storeBolt);

//        TridentTopology topology = new TridentTopology();
//        Stream stream = topology.newStream("spout1", lookupBolt);

//        stream.partitionPersist(factory,
//                fields,
//                new RedisStateUpdater(storeMapper).withExpire(86400000),
//                new Fields());
//
//        TridentState state = topology.newStaticState(factory);
//        stream = stream.stateQuery(state, new Fields("word"),
//                new RedisStateQuerier(lookupMapper),
//                new Fields("columnName","columnValue"));
    }
}
