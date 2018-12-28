package com.giggs;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class EsUtil {
    private static final Logger logger = Logger.getLogger(EsUtil.class);
    TransportClient client = null;

    public synchronized TransportClient getClient(String ip, int port) throws UnknownHostException {
        if(client==null){
            Settings settings = Settings.builder()
                    .put("cluster.name", "master001")
                    .put("client.transport.sniff", true)//自动发现其他节点
                    .build();

            client = new PreBuiltTransportClient(settings);
            client.addTransportAddresses(
                    new InetSocketTransportAddress(InetAddress.getByName(ip),port));
//            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip),port));

        }

        return client;
    }

    public void insertData(ArrayList<HashMap> records,String indexName,String typeName ) throws UnknownHostException {
        String ip=null;
        int port=9300;
        TransportClient client = getClient(ip,port);
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for(HashMap record:records){
            bulkRequest.add(client.prepareIndex(indexName,typeName).setSource(record));
        }
        client.close();
        BulkResponse responses = bulkRequest.get();
        if(responses.hasFailures()){
            logger.error("an error occur,please check the server log ");
            responses.buildFailureMessage();
        }

        logger.info( "load data success!" );

    }
    public void createIndex(String indexName,String mapping_json) throws UnknownHostException {
        if(isIndexExist(indexName)){
            logger.warn(String.format("index %s exists", indexName));
            return;
        }
        String ip = null;
        int port = 9300;
        TransportClient client = getClient(ip, port);
        client.admin().indices().preparePutMapping(indexName)
                .setSource(mapping_json, XContentType.JSON).execute().actionGet();
    }

    public boolean isIndexExist(String indexName) throws UnknownHostException {
        String ip = null;
        int port = 9300;
        TransportClient client = getClient(ip, port);
        IndicesExistsResponse response = client.admin()
                                                .indices()
                                                .exists(new IndicesExistsRequest()
                        .indices(new String[] { indexName })).actionGet();
        return response.isExists();
    }


}
