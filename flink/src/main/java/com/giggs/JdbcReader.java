package com.giggs;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.netty4.io.netty.util.concurrent.Future;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class JdbcReader extends RichAsyncFunction<String, Tuple2<String, String>> {

//    private transient DatabaseClient client;
    private Connection connection = null;
    private PreparedStatement ps = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        Class.forName("");//加载数据库驱动
        connection = DriverManager.getConnection("", "root", "root");//获取连接
        ps = connection.prepareStatement("");

    }

    @Override
    public void close() throws Exception {
        try {
            super.close();
            if (connection != null) {
                connection.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

    }

    @Override
    public void asyncInvoke(String key, final ResultFuture<Tuple2<String, String>> resultFuture) throws Exception {
//        try {
//            ResultSet resultSet = ps.executeQuery();
//            while (resultSet.next()) {
//                String name = resultSet.getString("nick");
//                String id = resultSet.getString("user_id");
//                Tuple2<String,String> tuple2 = new Tuple2<>();
//                tuple2.setFields(id,name);
//                resultFuture.complete((Collection<Tuple2<String, String>>) tuple2);
////                resultFuture.collect(tuple2);//发送结果，结果是tuple2类型，2表示两个元素，可根据实际情况选择
//            }
//        } catch (Exception e) {
//            System.out.println(e.getStackTrace());
//        }

        // issue the asynchronous request, receive a future for result
//        final Future<String> result = client.query(key);
        ResultSet resultSet = ps.executeQuery();
//         set the callback to be executed once the request by the client is complete
//         the callback simply forwards the result to the result future
        CompletableFuture.supplyAsync(new Supplier<String>() {

            @Override
            public String get() {
                return "";
//                try {
//
//                } catch (InterruptedException | ExecutionException e) {
//                    // Normally handled explicitly.
//                    return null;
//                }
            }
        }).thenAccept( (String dbResult) -> {
            resultFuture.complete(Collections.singleton(new Tuple2<>(key, dbResult)));
        });
    }
}
