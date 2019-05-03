package cn.leancloud.demo.task;

import cn.leancloud.common.Const;
import cn.leancloud.util.UrlTool;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import java.util.ArrayList;
import java.util.List;

public class MessageHandler {

    public static void saveLiveMessage(List<LiveMessage> messageList) throws AVException {
        List<AVObject> objectList = new ArrayList<>(messageList.size());
        for (LiveMessage message : messageList) {
            AVQuery<Live> liveAVQuery = new AVQuery<>("Live");
            liveAVQuery.whereEqualTo("sourceObjectId", message.getId());
            Live object = liveAVQuery.getFirst();
            // 加入需要更新的消息
            if (object == null) {
                object = assembleAVObject(message);
            } else {
                if (object.getStatus() == 0 && message.getLiveStatus() == 1) {
                    UrlTool.sendMessageViaServerChan(Const.SCKEY, message.getTitle(), "正在直播！");
                }
                object.put("title", message.getTitle());
                object.put("status", message.getLiveStatus());
                object.put("sourceObjectId", message.getId());
            }
            objectList.add(object);
        }
        // 批量添加
        AVObject.saveAll(objectList);
    }

    private static Live assembleAVObject(LiveMessage message) {
        Live result = new Live();
        result.put("title", message.getTitle());
        result.put("status", message.getLiveStatus());
        result.put("sourceObjectId", message.getId());
        return result;
    }


    public LiveMessage getLiveMessage(JSONObject jsonObject, int categoryId, String objId, String url) {
        if (jsonObject == null) {
            return null;
        }
        if (categoryId == 200) {
            // bilibili 直播
            return getBilibiliMessage(jsonObject, "bilibili", objId);
        }
        if (categoryId == 201) {
            // douyu 直播
            return getDouyuMessage(jsonObject, "斗鱼直播", objId);
        }
        if (categoryId == 202) {
            // CC 直播
            return getCCMessage(jsonObject, "CC直播", objId, url);
        }
        return null;
    }

    /**
     * CategoryId = 200
     * 获取 Bilibili 直播间信息
     * api 格式为: https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=${uid}
     *
     * @param jsonObject 根据 api 获得的 json 数据
     * @param name       直播站点名称
     * @param objId      订阅源的 ObjectId
     * @return 封装好的 LiveMessage
     */
    private LiveMessage getBilibiliMessage(JSONObject jsonObject, String name, String objId) {
        LiveMessage message = new LiveMessage();
        JSONObject data = jsonObject.getJSONObject("data");
        message.setCode(jsonObject.getInteger("code"));
        message.setMsg(jsonObject.getString("msg"));
        message.setMsgDetail(jsonObject.getString("message"));
        message.setLiveStatus(data.getInteger("liveStatus"));
        message.setTitle(data.getString("title") + " - " + name);
        message.setId(objId);
        return message;
    }

    /**
     * CategoryId = 201
     * 获取斗鱼直播间的信息
     * api 格式为: https://yuba.douyu.com/wbapi/web/group/getLiveInfo?uid=${鱼吧获取的uid}
     *
     * @param jsonObject 根据 api 获得的 json 数据
     * @param name       直播站点名称
     * @param objId      订阅源的 ObjectId
     * @return 封装好的 LiveMessage
     */
    private LiveMessage getDouyuMessage(JSONObject jsonObject, String name, String objId) {
        LiveMessage message = new LiveMessage();
        JSONObject data = jsonObject.getJSONObject("data");
        message.setCode(jsonObject.getInteger("status_code"));
        message.setMsg(jsonObject.getString("message"));
        message.setTitle(data.getString("room_name") + " - " + name);
        int status = data.getInteger("show_status");
        if (status == 1) {
            message.setLiveStatus(1);
        } else {
            message.setLiveStatus(0);
        }
        message.setId(objId);
        return message;
    }

    /**
     * Category 202
     *
     * @param jsonObject 根据 api 获得的 json 数据
     * @param name       直播站点名称
     * @param objId      订阅源的 ObjectId
     * @param url        订阅源的 URL, 用来定位 CC 直播间信息
     * @return 封装好的 LiveMessage
     */
    private LiveMessage getCCMessage(JSONObject jsonObject, String name, String objId, String url) {
        LiveMessage message = new LiveMessage();
        String uid = url.substring(url.lastIndexOf("=") + 1);
        JSONObject data = jsonObject.getJSONObject("data").getJSONObject(uid);
        message.setCode(200);
        message.setMsg("无");
        message.setTitle(data.getString("nickname") + " - " + name);
        message.setLiveStatus(data.getIntValue("is_live") == 1 ? 1 : 0);
        return message;
    }
}
