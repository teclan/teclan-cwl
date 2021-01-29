package com.teclan.cwl.ssq;

import com.teclan.cwl.db.DBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ssq {
    private final static Logger LOGGER = LoggerFactory.getLogger(Ssq.class);

    public static void insert(String date,String red,String blue){

        long count  =DBFactory.getDb().count("ssq","date = ?",date);
        if(count>0){
            LOGGER.info("正在处理，期数：{}，开奖号码:{},该期数已存在，调过...",date,String.format("%s-%s",red,blue));
            return;
        }
        LOGGER.info("正在处理，期数：{}，开奖号码:{}",date,String.format("%s-%s",red,blue));

        DBFactory.getDb().exec("insert ssq (`date`,`code`) values (?,?)",date,String.format("%s-%s",red,blue));

        int index =1;
        for(String r:red.split(",")){
            DBFactory.getDb().exec("insert ssq_detail (`date`,`code`,`order`,`color`) values (?,?,?,?)",date,r,index++,"red");
        }
        DBFactory.getDb().exec("insert ssq_detail (`date`,`code`,`order`,`color`) values (?,?,?,?)",date,blue,index++,"blue");
    }
}
