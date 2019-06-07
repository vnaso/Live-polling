package cn.leancloud.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * UrlToolTest
 *
 * @author vnaso
 * @date 2019/6/1
 */
public class UrlToolTest {
    @Test
    public void parseIpList() {
        String ipList = "210.209.82.190:8888,23.101.2.213:443,58.82.202.200:80,118.143.37.170:50006,124.160.56.76:36904,221.126.249.101:8080,47.52.27.97:31280,47.52.67.59:8080,65.52.174.40:80,118.140.151.98:3128,202.183.32.182:80,34.92.4.225:80,23.101.3.33:3128,63.220.1.43:80,202.183.32.173:80,47.52.68.55:3128,202.183.32.200:80,23.101.2.213:8080,210.209.82.142:8888,103.230.35.222:3128,160.19.49.138:8080,221.126.249.100:8080,119.28.87.177:808,47.75.218.161:3128,221.126.249.99:8080,221.126.249.102:8080,47.91.137.211:3128,202.183.32.181:80";
        String[] data = ipList.split(",");
        List<String> arrayList = Arrays.asList(data);
        System.out.println(arrayList.toString());
    }

    @Test
    public void jsonEncoding(){
        String s = " {\"msg\":\"\\u83b7\\u5f97\\u4e86 69 MB\\u6d41\\u91dicf.\",\"ret\":1}";
        JSONObject o = JSON.parseObject(s);
        String msg = o.getString("msg");

        System.out.println(msg);
    }
}