package com.teclan.cwl.db;

import com.teclan.cwl.config.CommonConfig;
import com.typesafe.config.Config;
import org.javalite.activejdbc.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DBFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBFactory.class);

    private static   String DRIVER_CLASS = "";
    private static   String URL = "";
    private static   String USER = "";
    private static   String PASSWORD = "";
    private static String dbName = "default";

    private static DB db;

    static {
        Config config = CommonConfig.getConfig();
        DRIVER_CLASS = config.getString("driver");
        URL = config.getString("url");
        USER = config.getString("username");
        PASSWORD = config.getString("password");
    }


    public static DB getDb() {
        return getDb(dbName);
    }

    public static DB getDb(String dbName) {

        if (db == null) {
            db = new DB(dbName);
        }
        return db;
    }


    public static void open(String dbName) {

        getDb(dbName).open(DRIVER_CLASS, URL, USER, PASSWORD);
        LOGGER.info("\n打开数据库成功...");
    }

    public static void close(String dbName){
        getDb(dbName).close();
        LOGGER.info("\n关闭数据库成功...");
    }


    public static void open() {
       open(dbName);
    }

    public static void close(){
       close(dbName);
    }
    
    public static Connection getConnection() {
    	return getDb().getConnection();
    }

}
