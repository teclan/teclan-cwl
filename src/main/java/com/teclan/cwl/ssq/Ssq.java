package com.teclan.cwl.ssq;

import com.teclan.cwl.db.DBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ssq {
    private final static Logger LOGGER = LoggerFactory.getLogger(Ssq.class);

    public static void insert(String date,String red,String blue){

        DBFactory.getDb().exec("insert ssq (`date`,`code`) values (?,?)",date,String.format("%s-%s",red,blue));

        for(String r:red.split(",")){
            DBFactory.getDb().exec("insert ssq_detail (`date`,`code`,`color`) values (?,?,?)",date,r,"red");
        }
        DBFactory.getDb().exec("insert ssq_detail (`date`,`code`,`color`) values (?,?,?)",date,blue,"blue");
    }
}
