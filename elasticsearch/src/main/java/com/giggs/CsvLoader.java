package com.giggs;

import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CsvLoader {

    public void csvToEs(String path,int skipNum,String split) throws IOException{
        EsUtil esClient = new EsUtil();
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        int i = 0;
        int batchSize = 4000;
        bufferedReader.skip(skipNum);
        ArrayList<HashMap> batch =new ArrayList<>();
        while (bufferedReader.ready()){
            if(i<batchSize){
                String[] line = bufferedReader.readLine().split(split);
                int x=0;
                HashMap tmp = new HashMap();
                for(String column:getSchema("")){
                    tmp.put(column,line[x]);
                }
                batch.add(tmp);
            }else {
                try{
                    esClient.insertData(batch,"","");
                    batch.clear();
                    i=0;
                }catch (Exception e1){
                    e1.printStackTrace();
                    System.out.println("");
                }


            }

        }



    }
    public String[] getSchema(String path){
        String []columns = null;
        columns = "".split("||");
        return columns;
    }

    public static ArrayList<String[]> readContent(String path,int skipnum,String split) throws IOException {
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        int i = 0;
        bufferedReader.skip(skipnum);
        ArrayList<String[]> batchRecords = new ArrayList<>();
        while (bufferedReader.ready()){
            batchRecords.add(bufferedReader.readLine().split(split));
        }
        return batchRecords;


    }
    public void superReader() throws FileNotFoundException {
        ICsvMapReader csvReader = new CsvMapReader(new FileReader(""), CsvPreference.STANDARD_PREFERENCE);


    }

}
