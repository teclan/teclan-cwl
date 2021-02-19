package com.teclan.cwl.ssq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teclan.cwl.db.DBFactory;
import com.teclan.cwl.utils.HttpUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SsqTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(SsqTest.class);

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

    @Test
    public void fetch() throws IOException {

        String url = "http://www.cwl.gov.cn/cwl_admin/kjxx/findDrawNotice?name=ssq&issueCount=100";
        JSONObject response = HttpUtils.get(url);
        JSONArray result = response.getJSONArray("result");
        for (int i = 0; i < result.size(); i++) {
            JSONObject o = result.getJSONObject(i);
            String date = o.getString("date");
            date = date.substring(0,10);
            String red = o.getString("red");
            String blue = o.getString("blue");
            Ssq.insert(date, red, blue);
        }
    }

    @Test
    public void  analyzer(){
        Ssq.analyzer();
    }

    @Test
    public void his(){
        Ssq.his();
    }
}
