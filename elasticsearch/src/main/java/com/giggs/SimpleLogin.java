package com.giggs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;


public class SimpleLogin {

    public static synchronized void login(String username,String keytab,String Krb5,Configuration config) throws IOException {
        String jaas = "java.security.krb5.conf";
        System.setProperty(jaas, Krb5);
        UserGroupInformation.setConfiguration(config);
        UserGroupInformation.loginUserFromKeytab(username, keytab);

    }

}
