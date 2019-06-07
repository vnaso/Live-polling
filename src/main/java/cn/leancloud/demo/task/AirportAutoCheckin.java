package cn.leancloud.demo.task;

import cn.leancloud.EngineFunction;
import cn.leancloud.EngineFunctionParam;
import cn.leancloud.common.Const;
import cn.leancloud.demo.todo.AppInitListener;
import cn.leancloud.util.UrlTool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AirportAutoCheckin
 *
 * @author vnaso
 * @date 2019/6/1
 */
public class AirportAutoCheckin {
    private final static  String loginUrl = "https://www.cordcloud.cc/auth/login";
    private final static String checkinUrl = "https://www.cordcloud.cc/user/checkin";
    private static final Logger logger = LogManager.getLogger(AppInitListener.class);

    private static List<String> getProxyList() throws AVException {
        AVQuery<IpProxy> query = new AVQuery<>("IpProxy");
        query.whereNotEqualTo("proxyList","");
        IpProxy ipProxy = query.getFirst();
        return UrlTool.parseIpList(ipProxy.getProxyList());
    }

    @EngineFunction("checkin")
    public static void doCheckin(@EngineFunctionParam("email")String email,
                                 @EngineFunctionParam("passwd")String passwd){
        List<String> proxyList = null;
        try {
            proxyList = getProxyList();
        } catch (AVException e) {
            e.printStackTrace();
            UrlTool.sendMessageViaServerChan(Const.SCKEY,"获取代理列表失败","AVException");
        }
        if(proxyList == null || proxyList.size() < 1){
            UrlTool.sendMessageViaServerChan(Const.SCKEY,"获取代理列表失败","数目为0");
            return;
        }
        Map<String,String> data = new HashMap<>(8);
        data.put("email", email);
        data.put("passwd", passwd);
        Map<String, String> cookies = null;
        Connection login = Jsoup.connect(loginUrl)
                .method(Connection.Method.POST)
                .data(data);
        Connection checkin = Jsoup.connect(checkinUrl)
                .method(Connection.Method.POST);
        // 循环切换 proxy 签到
        boolean checked = false;
        Connection.Response rst = null;
        for (String proxy : proxyList) {
            String ip = proxy.split(":")[0];
            int port = Integer.parseInt(proxy.split(":")[1]);
            login.proxy(ip,port);
            checkin.proxy(ip,port);
            try{
                Connection.Response response = login.execute();
                cookies = response.cookies();
                checkin.cookies(cookies);
                rst = checkin.execute();
            }catch (IOException e){
                logger.log(Level.INFO,"自动签到执行失败: " + e);
                continue;
            }
            logger.log(Level.INFO,"自动签到执行成功");
            checked = true;
            break;
        }
        if(checked && rst != null){
            String str = rst.body();
            JSONObject obj = JSON.parseObject(str);
            String msg = obj.getString("msg");
            if(StringUtil.isBlank(msg)){
                msg = rst.body();
            }
            UrlTool.sendMessageViaServerChan(Const.SCKEY,"自动签到成功",msg);
            logger.log(Level.INFO,"发送消息: " + msg);
        }else{
            UrlTool.sendMessageViaServerChan(Const.SCKEY,"自动签到失败","所有代理均失败, 请更新代理");
        }
    }

}
