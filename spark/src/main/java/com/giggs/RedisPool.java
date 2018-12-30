package com.giggs;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.Serializable;

public class RedisPool implements Serializable {
    JSONObject config ;
    JedisPoolConfig redisConfig = new JedisPoolConfig();

    String host;
    int port ;
    int poolSize ;
    JedisPool pool = null;

    public RedisPool(String path) throws IOException {
        config = ConfigUtil.getConfig(path);
        host= (String )config.get("redis.host");
        poolSize = (int )config.get("redis.poolsize");
        port = (int )config.get("redis.port");
    }

    private void makePool(){
        redisConfig.setMaxTotal(poolSize);
        redisConfig.setMaxWaitMillis(3000);
        pool = new JedisPool(redisConfig,host,port);
    }
    public Jedis getClient(){
        if(pool==null){
            makePool();
        }
        return pool.getResource();
    }

}
