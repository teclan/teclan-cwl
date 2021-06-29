package com.teclan.cwl.happy8;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teclan.cwl.db.DBFactory;
import com.teclan.cwl.utils.FileUtils;
import com.teclan.cwl.utils.HttpUtils;
import org.javalite.activejdbc.RowListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Happy8Test {
    private final static Logger LOGGER = LoggerFactory.getLogger(Happy8Test.class);
    private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    V[] vs = new V[80];

    public class V{
        public int o; //  号码
        public int count = 0;// 历史出现次数
        public String date1=""; // 最近开奖日期1
        public String date2=""; // 最近开奖日期1
        public String date3=""; // 最近开奖日期1
        public String date4=""; // 最近开奖日期1
        public String date5=""; // 最近开奖日期1


        public int compare(V v){
            if(this.count>v.count){
                return 1;
            }else if(this.count==v.count){
                return 0;
            }else{
                return -1;
            }
        }

        public String toString(){
            return String.format("号码：%s，开奖次数：%s，最近五次开奖日期：%s、%s，%s，%s，%s",o,count,date1,date2,date3,date4,date5);
        }
    }


    @Test
    public void analyzer(){
        DBFactory.getDb().find("select * from happy8 order by date desc", new RowListener() {
            public boolean next(Map<String, Object> row) {
               push(row);
                return true;
            }
        });

        sort(vs);

        for(V v:vs){
            if(v==null){
                continue;
            }
            LOGGER.info(v.toString());
            FileUtils.write2File("快乐八开奖分析_"+SDF.format(new Date())+".txt",v.toString());
        }

    }

    private void sort(V[] vs){
        for(int i=0;i<vs.length-1;i++){
            if(vs[i]==null){
                continue;
            }
            for(int j=i+1;j<vs.length-1;j++){
                if(vs[j]==null){
                    continue;
                }
                if(vs[j].compare(vs[i])>0){
                    V tmp = vs[i];
                    vs[i] = vs[j];
                    vs[j] = tmp;
                }
            }
        }
    }

    private void push(Map<String, Object> row){
        for(int i=1;i<=20;i++){
            int index = i;
            int o = (Integer)row.get("v"+index);
            String date = row.get("date").toString();

            boolean found =false;
            int j=0;
            for(j=0;j<vs.length-1;j++){
                V v = vs[j];
                if (v!=null && v.o==o){
                    v.count++;

                    if(v.date1==""){
                        v.date1=date;
                    }else if(v.date2==""){
                        v.date2=date;
                    }else if(v.date3==""){
                        v.date3=date;
                    }else if(v.date4==""){
                        v.date4=date;
                    }else if(v.date5==""){
                        v.date5=date;
                    }
                    found = true;
                }

            }

            if(!found){
                for(int m =0;m<vs.length-1;m++){
                    if(vs[m]==null){
                        V v = new V();
                        v.o=o;
                        v.count++;
                        v.date1=date;
                        vs[m]=v;
                        break;
                    }
                }



            }
        }
    }




    @Test
    public void fetch() throws IOException {

        String url = "http://www.cwl.gov.cn/cwl_admin/kjxx/findDrawNotice?name=kl8&issueCount=100";
        JSONObject response = HttpUtils.get(url);
        JSONArray result = response.getJSONArray("result");
        for (int i = 0; i < result.size(); i++) {

            JSONObject o = result.getJSONObject(i);
            String code = o.getString("code");
            String date = o.getString("date");
            String red = o.getString("red");
            String[] v = red.split(",");
            long count = DBFactory.getDb().count("happy8","`code`=?",code);
            if(count>0){
                LOGGER.info("已录：期数{},日期{}，数据：{}",code,date,red);
                continue;
            }

            DBFactory.getDb().exec("insert into happy8 (`code`,`date`,v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,v11,v12,v13,v14,v15,v16,v17,v18,v19,v20) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ",
                 code,date, v[0],v[1],v[2],v[3],v[4],v[5],v[6],v[7],v[8],v[9],v[10],v[11],v[12],v[13],v[14],v[15],v[16],v[17],v[18],v[19]
            );
            LOGGER.info("新增：期数{},日期{}，数据：{}",code,date,red);
        }

    }

    @Test
    public void fetchHis() throws IOException {

        int issueEnd = 2021168;

        //String url = "http://www.cwl.gov.cn/cwl_admin/kjxx/findDrawNotice?name=kl8&issueCount=&issueStart=2019011&issueEnd=2021011&dayStart=&dayEnd=&pageNo=";

        while(issueEnd>2017041) {
            String url = "http://www.cwl.gov.cn/cwl_admin/kjxx/findDrawNotice?name=kl8&issueCount=&issueStart=" + (issueEnd - 2) + "&issueEnd=" +(issueEnd-1) + "&dayStart=&dayEnd=&pageNo=";
            issueEnd -=2;
            JSONObject response = HttpUtils.get(url);
            JSONArray result = response.getJSONArray("result");
            for (int i = 0; i < result.size(); i++) {
                JSONObject o = result.getJSONObject(i);
                String code = o.getString("code");
                String date = o.getString("date");
                String red = o.getString("red");
                String[] v = red.split(",");
                DBFactory.getDb().exec("insert into happy8 (`code`,`date`,v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,v11,v12,v13,v14,v15,v16,v17,v18,v19,v20) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ",
                        code, date, v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19]
                );
                LOGGER.info("期数{},日期{}，数据：{}", code, date, red);
            }
        }
    }




    @Before
    public void setUp(){

        Map<String, String> header = new HashMap<String, String>();
        header.put("Accept", "application/json, text/javascript, */*; q=0.01");
        header.put("Accept-Encoding", "gzip, deflate");
        header.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        header.put("Connection", "keep-alive");
        header.put("Cookie", "UniqueID=fUxwXD6mIDyEr9Yy1611907284860; Sites=_21; 21_vq=11; _ga=GA1.3.1605680773.1611907291; _gid=GA1.3.1323908680.1611907291");
        header.put("Host", "www.cwl.gov.cn");
        header.put("Referer", "http://www.cwl.gov.cn/kjxx/ssq/kjgg/");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:85.0) Gecko/20100101 Firefox/85.0");
        header.put("X-Requested-With", "XMLHttpRequest");


        HttpUtils.setHeaders(header);


        DBFactory.open();
    }

    @After
    public void setDown(){
        DBFactory.close();
    }



}
