package com.giggs;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class Test001 {
    public static void main(String[] args) throws UnknownHostException {
        TransportClient client ;
        String ip = "192.168.6.7";
        int port=9200;
        Settings settings = Settings.builder()
                .put("cluster.name", "master001")
                .put("client.transport.sniff", true)//自动发现其他节点
                .build();

        client = new PreBuiltTransportClient(settings);
        client.addTransportAddresses(
                new InetSocketTransportAddress(InetAddress.getByName(ip),port));
        List<TransportAddress> addrs = client.transportAddresses();
        System.out.println(addrs);
        for(TransportAddress a :addrs){
            System.out.println(a.getAddress());
        }
        List<DiscoveryNode> nodes = client.listedNodes();
        for(DiscoveryNode node:nodes){
            System.out.println(node.getAddress());
        }

    }
}
