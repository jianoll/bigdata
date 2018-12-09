package com.jxjr.storm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.sql.*;
import java.util.Map;

public class MysqlSpout extends BaseRichSpout {
    String url = "jdbc:mysql:/127.0.0.1:3306/investment";
    String username = "root";
    String password = "root";
    ResultSet res;
    Statement sta;
    SpoutOutputCollector collector;
    int id = 0;

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        try {
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, "root", "root");
            sta = conn.createStatement();
            res = sta.executeQuery("select proj_id,proj_name,total_investment,build_area from investdetail");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.collector = collector;
    }

    @Override
    public void nextTuple() {
        String str = "";
        try {
            if (res.next()) {
                String proj_id = res.getString(1);
                String proj_name = res.getString(2);
                int total_investment = res.getInt(3);
                String build_area = res.getString(4);
                str += proj_id+"\t"+proj_name+"\t"+total_investment+"\t"+build_area;
                collector.emit(new Values(str));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("investdetail"));
    }
}
