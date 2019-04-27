package cn.leancloud.demo.task;

import com.alibaba.fastjson.JSONObject;

public class LiveMessage{
        private String id;
        private int code;
        private String msg;
        private String msgDetail;
        private JSONObject data;
        private int liveStatus;
        private String title;

        public void setId(String id) {
                this.id = id;
        }

        public String getId() {
                return id;
        }

        public int getCode() {
                return code;
        }

        public void setCode(int code) {
                this.code = code;
        }

        public String getMsg() {
                return msg;
        }

        public void setMsg(String msg) {
                this.msg = msg;
        }

        public String getMsgDetail() {
                return msgDetail;
        }

        public void setMsgDetail(String msgDetail) {
                this.msgDetail = msgDetail;
        }

        public JSONObject getData() {
                return data;
        }

        public void setData(JSONObject data) {
                this.data = data;
        }

        public int getLiveStatus() {
                return liveStatus;
        }

        public void setLiveStatus(int liveStatus) {
                this.liveStatus = liveStatus;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }
}
