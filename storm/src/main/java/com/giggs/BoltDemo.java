package com.jxjr.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;


///整体流程大概是这样的，先在prepare中接受穿进来的collector参数，用来发送数据，然后在declareOutputFields中声明输出的数据结构，
// 最后在execute中处理完数据后，按照声明的数据结构发送数据


public class BoltDemo implements IRichBolt {
    private OutputCollector collector;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;

    }

    @Override
    public void execute(Tuple tuple) {

        String content = tuple.getStringByField("sentence");
        String [] cut = content.split(" ");
//        this.collector.emit(new Values(cut[0], cut[1]));
        System.out.println(cut[0]);

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
//        outputFieldsDeclarer.declare(new Fields("word", "count"));

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
