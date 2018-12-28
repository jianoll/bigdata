package com.giggs;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class OracleLoader {
    private static final Logger logger = Logger.getLogger(OracleLoader.class);
    String pwd = "xxx";
    String user = "yyy";
    String driver = "oracle.jdbc.OracleDriver";
    String url = "jdbc:oracle:thin:@128.xx.xx.xx:1521/yyyyy";

    public OracleLoader() {
//        this.pwd = pwd;
    }
    public void orclToEs() throws ClassNotFoundException, UnknownHostException {
        EsUtil esClient = new EsUtil();
        OracleLoader reader = new OracleLoader();
        ArrayList<HashMap> records = reader.getData("");
        int batchSize=4000;
        int recordNum = records.size();
        int i = 0;
        ArrayList<HashMap> tmpRecord = new ArrayList<HashMap> ();
        while (i<recordNum){

            if (tmpRecord.size()<batchSize){
                tmpRecord.add(records.get(i));
                i+=1;
            }
            else {
                try{
                    esClient.insertData(tmpRecord,"","");
                    tmpRecord.clear();
                }catch (Exception e2){
                    e2.printStackTrace();
                }
            }
        }
    }

    //这种方式不能读取太大的记录集，不然会发生OOM，只是暂时用一下
    public ArrayList<HashMap> getData(String sql) throws ClassNotFoundException {
        Connection conn = null;
        ResultSet result = null;
        ArrayList results = new ArrayList();
        Class.forName(driver);
        while (true) {

            try {
                conn = DriverManager.getConnection(url, user, pwd);
                Statement statement = conn.createStatement();
                HashMap adminCode = new HashMap();
                result = statement.executeQuery(sql);
                ResultSetMetaData columnInfo = result.getMetaData();
                int columnNum = columnInfo.getColumnCount();
                while (result.next()) {
                    HashMap record = new HashMap();
                    for (int i = 1; i <= columnNum; i++) {
                        Object tmp = null;
                        tmp = result.getObject(i);
                        if (tmp == null) {
                            tmp = "0";
                        }
                        record.put(columnInfo.getColumnName(i), tmp);
                    }
                    results.add(record);
                }
                System.out.println(results);
                result.close();
                statement.close();
                String records = JSONObject.toJSONString(adminCode);
                System.out.println(records);
                conn.close();
                return results;
            } catch (Exception e) {
                logger.error(e.getStackTrace());
            }
            return results;

        }
    }
}
