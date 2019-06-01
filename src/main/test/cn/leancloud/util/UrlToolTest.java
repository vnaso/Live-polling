package cn.leancloud.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * UrlToolTest
 *
 * @author vnaso
 * @date 2019/6/1
 */
public class UrlToolTest {

    @Test
    public void extractProxy(){
        String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";
        String dataStr = "num: 100\n" +
                "port: \n" +
                "kill_port: \n" +
                "address: 香港\n" +
                "kill_address: \n" +
                "anonymity: \n" +
                "type: 1\n" +
                "post: 1\n" +
                "sort: 1\n" +
                "key: f97a13b8846c0c0b6f6067b23fb8e6fd";
        Map<String,String> data = UrlTool.kv2Map(dataStr);
        String url = "https://ip.ihuan.me/tqdl.html";
        Connection connection = Jsoup.connect(url)
                .timeout(10*1000)
                .header("user-agent",USER_AGENT)
                .data(data);
        Document document = null;
        try {
            document = connection.post();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(document!=null){
            Elements elements = document.select("br:contains(^\\d)");
            System.out.println(elements.text());
        }

    }

    @Test
    public void parseIpList() {
        String ipList = "210.209.82.190:8888,23.101.2.213:443,58.82.202.200:80,118.143.37.170:50006,124.160.56.76:36904,221.126.249.101:8080,47.52.27.97:31280,47.52.67.59:8080,65.52.174.40:80,118.140.151.98:3128,202.183.32.182:80,34.92.4.225:80,23.101.3.33:3128,63.220.1.43:80,202.183.32.173:80,47.52.68.55:3128,202.183.32.200:80,23.101.2.213:8080,210.209.82.142:8888,103.230.35.222:3128,160.19.49.138:8080,221.126.249.100:8080,119.28.87.177:808,47.75.218.161:3128,221.126.249.99:8080,221.126.249.102:8080,47.91.137.211:3128,202.183.32.181:80";
        String[] data = ipList.split(",");
        List<String> arrayList = Arrays.asList(data);
        System.out.println(arrayList.toString());
    }
}