package cn.leancloud.demo.task;

import cn.leancloud.util.UrlTool;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageHandlerTest {

    @Test
    public void getCCMessage() {
        String url = "http://cc.163.com/lives/anchor?uids=266756597";
        JSONObject jsonObject = UrlTool.getJsonByUrl(url);
        LiveMessage message = MessageHandler.getCCMessage(jsonObject,"CC直播","agshasd", url);
        System.out.println(message);
    }
}