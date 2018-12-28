package com.giggs;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigUtil {

    public static JSONObject  getConfig(String path) throws IOException {
//        String path = args[0];
        JSONObject json = (JSONObject) JSONObject.parse(readContent(path));
        return  json;
    }


    public static void print(String[] args) throws IOException {
        String path = "d:\\data\\airflow.cfg";
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = bufferedReader.lines().collect(Collectors.toList());
        for (String line : lines){
            System.out.println(line);
        }
    }

    public static String readContentV2(String path) throws FileNotFoundException {
//        path = "d:\\data\\airflow.cfg";
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        Stream lines = bufferedReader.lines();
        return lines.reduce((s1,s2)->s1.toString()+s2.toString()).get().toString();
    }


    public static String readContent(String path) throws IOException {
//        String path = "d:\\data\\airflow.cfg";
        FileReader fileReader = new FileReader(path);

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        int i = 0;
        String configbody = "";
        while (bufferedReader.ready()){
//            bufferedReader.skip(1000);
            configbody+=bufferedReader.readLine();
        }
            return configbody;
//        String escaped = StringEscapeUtils.unescapeJavaScript(configbody);
//        System.out.println(escaped);
//        return escaped;

    }

}
