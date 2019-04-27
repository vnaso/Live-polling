package cn.leancloud.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;

public class UrlTool {
    private static final Logger log = LogManager.getLogger(UrlTool.class);
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.96 Safari/537.36";

    /**
     * 根据api接口查询json数据并返回
     *
     * @param url api接口
     * @return
     */
    public static JSONObject getJsonByUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        Connection connection = Jsoup.connect(url).ignoreContentType(true).timeout(10000);
        Connection.Response response;
        try {
            response = connection.execute();
            if (response != null) {
                return JSONObject.parseObject(response.body());
            }
        } catch (Exception e) {
            log.error("针对网址[{}]的查询失败", url);
            return null;
        }
        return null;
    }

    /**
     * 调用ServerChan的接口向用户发送消息
     *
     * @param sckey ServerChan用户的sckey
     * @param title 发送消息的标题
     * @param desc  发送消息的内容
     */
    public static void sendMessageViaServerChan(String sckey, String title, String desc) {
        String url = "https://sc.ftqq.com/" + sckey + ".send";
        Map<String, String> data = new HashMap<>(5);
        data.put("text", title);
        data.put("desp", desc);
        Connection connection = Jsoup.connect(url).data(data);
        try {
            connection.post();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
