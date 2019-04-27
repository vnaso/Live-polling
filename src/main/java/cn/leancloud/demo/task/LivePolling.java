package cn.leancloud.demo.task;

import cn.leancloud.EngineFunction;
import cn.leancloud.util.UrlTool;

public class LivePolling {
    private static String SCKEY = "SCU27968T968c6ed25f2893356cc6493550fa72db5c6ac73571472";
    @EngineFunction("poll")
    public static void poll(){
        UrlTool.sendMessageViaServerChan(SCKEY,"test","from leancloud");
    }
}
