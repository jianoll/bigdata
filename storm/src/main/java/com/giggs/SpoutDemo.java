package com.jxjr.storm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.apache.storm.topology.IRichSpout;


import java.util.Map;

public class SpoutDemo  implements  IRichSpout {
    private SpoutOutputCollector collector;

    @Override
    public void open(Map map, org.apache.storm.task.TopologyContext topologyContext, org.apache.storm.spout.SpoutOutputCollector spoutOutputCollector) {

    }

    @Override
    public void close() {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void nextTuple() {

    }

    @Override
    public void ack(Object o) {

    }

    @Override
    public void fail(Object o) {

    }

    @Override
    public void declareOutputFields(org.apache.storm.topology.OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
