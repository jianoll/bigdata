package com.giggs;


import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import java.net.InetSocketAddress;
import java.util.List;

public class Binlog2Kafka {
    public static void main(String[] args) throws InterruptedException {
        // 第一步：与canal进行连接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("127.0.0.1", 11111),
                "example", "canal", "canal");
        connector.connect();
        connector.subscribe();
        while (true) {
            try {
                // 每次读取 1000 条
                Message message = connector.getWithoutAck(1000);
                long batchID = message.getId();
                int size = message.getEntries().size();
                if (batchID == -1 || size == 0) {
                    System.out.println("当前暂时没有数据");
                    Thread.sleep(1000);
                } else {
                    System.out.println("取到新数据......");
                    System.out.println(message.getEntries());
                    List<CanalEntry.Entry> entries =message.getEntries();
                    for(CanalEntry.Entry entry:entries){
                        System.out.println(entry.getHeader());
                        CanalEntry.Header msgHeader = entry.getHeader();
                        System.out.println(msgHeader.getTableName());
                        System.out.println(msgHeader.getEventType());
                        System.out.println(entry.getStoreValue());
                        System.out.println(entry);
                        System.out.println(entry.getStoreValue());

                    }

                }

                connector.ack(batchID);

            } catch (Exception e) {
                // TODO: handle exception

            } finally {
                Thread.sleep(1000);
            }
        }
    }


}
