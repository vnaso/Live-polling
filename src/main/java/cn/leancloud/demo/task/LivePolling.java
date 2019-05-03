package cn.leancloud.demo.task;

import cn.leancloud.EngineFunction;
import cn.leancloud.demo.todo.AppInitListener;
import cn.leancloud.util.UrlTool;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class LivePolling {
    private static final MessageHandler messageHandler = new MessageHandler();
    private static final Logger logger = LogManager.getLogger(AppInitListener.class);

    @EngineFunction("poll")
    public static void poll() {
        AVQuery<Source> query = new AVQuery<>("Source");
        query.whereEqualTo("state", true);
        List<Source> sourceList = null;
        try {
            sourceList = query.find();
        } catch (AVException e) {
            logger.error("查询source失败", e);
            e.printStackTrace();
        }
        if (sourceList == null) {
            logger.info("查询source失败");
            return;
        }

        List<LiveMessage> messageList = new ArrayList<>(sourceList.size());
        // 获取最新直播间状态
        for (Source source : sourceList) {
            JSONObject data = UrlTool.getJsonByUrl(source.getApiLink());
            LiveMessage message = messageHandler.getLiveMessage(data, source.getCategoryId(), source.getObjectId(), source.getApiLink());
            if (message != null) {
                messageList.add(message);
            }
        }
        // 更新并根据直播间状态判断是否提醒
        try {
            MessageHandler.saveLiveMessage(messageList);
        } catch (AVException e) {
            logger.error("批量更新消息失败", e);
            e.printStackTrace();
        }

    }
}
