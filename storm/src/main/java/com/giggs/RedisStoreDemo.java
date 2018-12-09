package com.jxjr.storm;

import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisLookupMapper;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Values;

import java.util.List;

public class RedisStoreDemo implements RedisStoreMapper {
    private RedisDataTypeDescription description;

    public RedisStoreDemo() {
        description = new RedisDataTypeDescription(
                RedisDataTypeDescription.RedisDataType.HASH, "redis_storm_hashkey");

    }

    @Override
    public RedisDataTypeDescription getDataTypeDescription() {
        return description;
    }

    @Override
    public String getKeyFromTuple(ITuple iTuple) {
        return iTuple.getStringByField("word");
    }

    @Override
    public String getValueFromTuple(ITuple iTuple) {
        return "20";
    }
}
