package com.teclan.cwl.ssq;

import com.teclan.cwl.db.DBFactory;
import com.teclan.cwl.utils.FileUtils;
import com.teclan.cwl.utils.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Ssq {
    private final static Logger LOGGER = LoggerFactory.getLogger(Ssq.class);
    private final static SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd");
    private final static int LIMIT=1;


    public static void insert(String date,String red,String blue){

        long count  =DBFactory.getDb().count("ssq","date = ?",date);
        if(count>0){
            LOGGER.info("正在处理，期数：{}，开奖号码:{},该期数已存在，跳过...",date,String.format("%s-%s",red,blue));
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
        new File(path).delete();

        List<Map> list =  DBFactory.getDb().findAll("select min(`date`)  v from ssq \n" +
                "union\n" +
                "select max(`date`)  v from ssq  \n" +
                "union\n" +
                "select count(*) v from ssq  ");

        String line = String.format("正在分析，样本：%s->%s 共 %s 期 ，各个序号出现频率最多的号码如下（前"+LIMIT+"）...\r\n",list.get(0).get("v"),list.get(1).get("v"),list.get(2).get("v"));
        LOGGER.info(line);
        FileUtils.write2File(path,line);

        String sql = "select * from (\n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =1 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,"+LIMIT+"\n" +
                ") t1\n" +
                "union\n" +
                "select * from (\n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =2 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,"+LIMIT+"\n" +
                ") t2\n" +
                "union\n" +
                "select * from (\n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =3 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,"+LIMIT+"\n" +
                ") t3\n" +
                "union\n" +
                "select * from (\n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =4 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,"+LIMIT+"\n" +
                ") t4\n" +
                "union\n" +
                "\tselect * from ( \n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =5 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,"+LIMIT+"\n" +
                ") t5\n" +
                "union\n" +
                "select * from (\n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =6 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,"+LIMIT+"\n" +
                ") t6\n"+
                "union\n" +
                "select * from (\n" +
                "\tselect t1.order,t1.code,max(time) time  from (\n" +
                "\t select `order`,`code`,count(*) time from ssq_detail where `order` =7 group by `order`,`code` \n" +
                "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,"+LIMIT+"\n" +
                ") t7\n";

        list =  DBFactory.getDb().findAll(sql);

      List<String> advice = new ArrayList<String>();
      for(Map map:list){

          List<Map> rows;
          if(Integer.valueOf(7).equals(map.get("order"))){
              line = String.format("蓝球，序号：%s,号码：%s,出现次数:%s",map.get("order"),map.get("code"),map.get("time"));
              //rows =  DBFactory.getDb().findAll("select `date` from ssq_detail sd  where `code` ='"+map.get("code")+"' and `color` ='blue' and `order` = '"+map.get("order")+"'");
          }else {
              line = String.format("红球，序号：%s,号码：%s,出现次数:%s",map.get("order"),map.get("code"),map.get("time"));
             // rows =  DBFactory.getDb().findAll("select `date` from ssq_detail sd  where `code` ='"+map.get("code")+"' and `color` ='red' and `order` = '"+map.get("order")+"'");
          }
//          line +=" | 出现日期： ";
//          for(Map m:rows){
//              line+=m.get("date")+" | ";
//          }

          line += "\r\n";
          LOGGER.info(line);

          FileUtils.write2File(path,line);

          advice.add(map.get("code").toString());
      }
        String blue = advice.remove(6);
        line += String.format("\r\n建议买入号码：%s", Objects.join(",",advice)+"-"+blue);
        LOGGER.info(line);
        FileUtils.write2File(path,line);
    }


    public static void his(){

           String filePath = "分析历史.txt";
           new File(filePath).delete();

            List<Map> openHis = DBFactory.getDb().findAll("select `date`,`code` from ssq ");

            for(int i=0;i<openHis.size()-1;i++){
                Map map = openHis.get(i);
                String date = map.get("date").toString();
                List<Map> analyzer = DBFactory.getDb().findAll(String.format(SQL,date,date,date,date,date,date,date));
                List<String> advice = new ArrayList<String>();
                for(Map m:analyzer){
                    advice.add(m.get("code").toString());
                }
                String blue = advice.remove(6);
                String pre = Objects.join(",",advice)+"-"+blue;// 预测开奖号码
                String actully = openHis.get(i+1).get("code").toString();//下期实际开奖号码

                String line = String.format("截止分析日期：%s，预测开奖号码:%s，下期 %s 实际开奖号码:%s，预测结果：%s \n\n",date,pre,openHis.get(i+1).get("date").toString(),actully,calculat(pre,actully));
                FileUtils.write2File(filePath,line);
            }
    }

    private static String calculat(String pre,String actully){

        String[] preRed = pre.split("-")[0].split(",");
        String preBlue = pre.split("-")[1];

        String[] actullyRed = actully.split("-")[0].split(",");
        String actullyBlue = actully.split("-")[1];

        int r = 0;
        for(int i=0;i<preRed.length;i++){
            for(int j=0;j<actullyRed.length;j++){
                   if(preRed[i].equals(actullyRed[j])){
                       r++;
                   }
            }
        }

        return String.format("红球命中个数：%s，篮球命中个数:%s",r,preBlue.equals(actullyBlue)?1:0);
    }

    private static final String SQL = "select * from (\n" +
            "\tselect t1.order,t1.code,max(time) time  from (\n" +
            "\t select `order`,`code`,count(*) time from ssq_detail where `order` =1 and `date` <='%s' group by `order`,`code` \n" +
            "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,1\n" +
            ") t1\n" +
            "union\n" +
            "select * from (\n" +
            "\tselect t1.order,t1.code,max(time) time  from (\n" +
            "\t select `order`,`code`,count(*) time from ssq_detail where `order` =2 and `date` <='%s' group by `order`,`code` \n" +
            "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,1\n" +
            ") t2\n" +
            "union\n" +
            "select * from (\n" +
            "\tselect t1.order,t1.code,max(time) time  from (\n" +
            "\t select `order`,`code`,count(*) time from ssq_detail where `order` =3 and `date` <='%s' group by `order`,`code` \n" +
            "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,1\n" +
            ") t3\n" +
            "union\n" +
            "select * from (\n" +
            "\tselect t1.order,t1.code,max(time) time  from (\n" +
            "\t select `order`,`code`,count(*) time from ssq_detail where `order` =4 and `date` <='%s' group by `order`,`code` \n" +
            "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,1\n" +
            ") t4\n" +
            "union\n" +
            "\tselect * from ( \n" +
            "\tselect t1.order,t1.code,max(time) time  from (\n" +
            "\t select `order`,`code`,count(*) time from ssq_detail where `order` =5 and `date` <='%s' group by `order`,`code` \n" +
            "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,1\n" +
            ") t5\n" +
            "union\n" +
            "select * from (\n" +
            "\tselect t1.order,t1.code,max(time) time  from (\n" +
            "\t select `order`,`code`,count(*) time from ssq_detail where `order` =6 and `date` <='%s' group by `order`,`code` \n" +
            "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,1\n" +
            ") t6\n" +
            "union\n" +
            "select * from (\n" +
            "\tselect t1.order,t1.code,max(time) time  from (\n" +
            "\t select `order`,`code`,count(*) time from ssq_detail where `order` =7 and `date` <='%s' group by `order`,`code` \n" +
            "\t) t1  group by t1.order,t1.code,t1.time order by t1.time desc limit 0,1\n" +
            ") t7\n";
}
