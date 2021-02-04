package com.teclan.cwl.ssq;

import com.teclan.cwl.db.DBFactory;
import com.teclan.cwl.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Ssq {
    private final static Logger LOGGER = LoggerFactory.getLogger(Ssq.class);
    private final static SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd");


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

    /**
     * 红球分析
     */
    public static void analyzer(){

        String path = "分析-"+SDF.format(new Date())+".txt";

        List<Map> list =  DBFactory.getDb().findAll("select min(`date`)  v from ssq \n" +
                "union\n" +
                "select max(`date`)  v from ssq  \n" +
                "union\n" +
                "select count(*) v from ssq  ");

        String line = String.format("正在分析，样本：%s->%s 共 %s 期 ，各个序号出现频率最多的号码如下（前5）...\r\n",list.get(0).get("v"),list.get(1).get("v"),list.get(2).get("v"));
        LOGGER.info(line);
        FileUtils.write2File(path,line);
        list =  DBFactory.getDb().findAll("select * from (\n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =1 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,5\n" +
                ") t1\n" +
                "union\n" +
                "select * from (\n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =2 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,5\n" +
                ") t2\n" +
                "union\n" +
                "select * from (\n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =3 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,5\n" +
                ") t3\n" +
                "union\n" +
                "select * from (\n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =4 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,5\n" +
                ") t4\n" +
                "union\n" +
                "\tselect * from ( \n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =5 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,5\n" +
                ") t5\n" +
                "union\n" +
                "select * from (\n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =6 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,5\n" +
                ") t6\n"+
               "union\n" +
              "select * from (\n" +
              "\tselect t1.order,t1.code,max(time) time  from (\n" +
              "\t select `order`,`code`,count(*) time from ssq_detail where `order` =7 group by `order`,`code` \n" +
              "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,5\n" +
              ") t7\n");

      for(Map map:list){

          List<Map> rows;
          if(Integer.valueOf(7).equals(map.get("order"))){
              line = String.format("蓝球，序号：%s,号码：%s,出现次数:%s",map.get("order"),map.get("code"),map.get("time"));
              rows =  DBFactory.getDb().findAll("select `date` from ssq_detail sd  where `code` ='"+map.get("code")+"' and `color` ='blue' and `order` = '"+map.get("order")+"'");
          }else {
              line = String.format("红球，序号：%s,号码：%s,出现次数:%s",map.get("order"),map.get("code"),map.get("time"));
              rows =  DBFactory.getDb().findAll("select `date` from ssq_detail sd  where `code` ='"+map.get("code")+"' and `color` ='red' and `order` = '"+map.get("order")+"'");
          }
          line +=" | 出现日期： ";
          for(Map m:rows){
              line+=m.get("date")+" | ";
          }

          line += "\r\n";
          LOGGER.info(line);

          FileUtils.write2File(path,line);
      }
    }
}
