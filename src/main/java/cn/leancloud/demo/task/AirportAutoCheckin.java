package cn.leancloud.demo.task;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
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
import java.net.HttpCookie;
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
    private final static String loginUrl = "https://www.cordcloud.cc/auth/login";
    private final static String checkinUrl = "https://www.cordcloud.cc/user/checkin";
    private static final Logger logger = LogManager.getLogger(AppInitListener.class);
    private static int checkState = 0;

    private static List<String> getProxyList() throws AVException {
        AVQuery<IpProxy> query = new AVQuery<>("IpProxy");
        query.whereNotEqualTo("proxyList", "");
        IpProxy ipProxy = query.getFirst();
        return UrlTool.parseIpList(ipProxy.getProxyList());
    }

    @EngineFunction("checkin")
    public static void doCheckin(@EngineFunctionParam("email") String email,
                                 @EngineFunctionParam("passwd") String passwd,
                                 @EngineFunctionParam("notify") boolean notify) {
        // 自动签到控制
        if (checkState == Const.CheckState.CHECKED.getCode()) {
            logger.log(Level.INFO, "已签到, 不再重复执行");
            return;
        } else if (checkState == Const.CheckState.CHECKING.getCode()) {
            logger.log(Level.INFO, "签到中");
        } else if (checkState == Const.CheckState.UNCHECKED.getCode()) {
            logger.log(Level.INFO, "开始签到");
            checkState = Const.CheckState.CHECKING.getCode();
        }
        // 获取 proxy 列表
        List<String> proxyList = null;
        try {
            proxyList = getProxyList();
        } catch (AVException e) {
            e.printStackTrace();
            UrlTool.sendMessageViaServerChan(Const.SCKEY, "获取代理列表失败", "AVException");
        }
        if (proxyList == null || proxyList.size() < 1) {
            UrlTool.sendMessageViaServerChan(Const.SCKEY, "获取代理列表失败", "数目为0");
            return;
        }
        Map<String, String> data = new HashMap<>(8);
        data.put("email", email);
        data.put("passwd", passwd);
        Map<String, String> cookies = null;
        // 创建链接
        Connection login = Jsoup.connect(loginUrl)
                .method(Connection.Method.POST)
                .data(data);
        Connection checkin = Jsoup.connect(checkinUrl)
                .method(Connection.Method.POST);
        // 循环切换 proxy 签到
        Connection.Response rst = null;
        for (String proxy : proxyList) {
            String ip = proxy.split(":")[0];
            int port = Integer.parseInt(proxy.split(":")[1]);
            login.proxy(ip, port);
            checkin.proxy(ip, port);
            try {
                Connection.Response response = login.execute();
                cookies = response.cookies();
                checkin.cookies(cookies);
                rst = checkin.execute();
            } catch (IOException e) {
                logger.log(Level.INFO, "自动签到执行失败: " + e);
                continue;
            }
            logger.log(Level.INFO, "自动签到执行成功");
            checkState = Const.CheckState.CHECKED.getCode();
            break;
        }
        if (checkState == Const.CheckState.CHECKED.getCode() && rst != null) {
            String str = rst.body();
            JSONObject obj = JSON.parseObject(str);
            String msg = obj.getString("msg");
            if (StringUtil.isBlank(msg)) {
                msg = rst.body();
            }
            // 控制是否发送消息
            if (notify) {
                UrlTool.sendMessageViaServerChan(Const.SCKEY, "自动签到成功", msg);
                logger.log(Level.INFO, "发送消息: " + msg);
            }
        } else {
            UrlTool.sendMessageViaServerChan(Const.SCKEY, "自动签到失败", "所有代理均失败, 请更新代理");
        }
    }

    @EngineFunction("checkinWithoutProxy")
    public static void checkinWithoutProxy(@EngineFunctionParam("email") String email,
                                    @EngineFunctionParam("passwd") String passwd,
                                    @EngineFunctionParam("notify") boolean notify,
                                    @EngineFunctionParam("loginUrl") String loginUrl,
                                    @EngineFunctionParam("checkinUrl") String checkinUrl
    ) {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36";
        boolean checked = false;
        // login
        HttpResponse loginRst;
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(10 * 1000);
                loginRst = HttpRequest.post(loginUrl)
                        .header(Header.USER_AGENT, userAgent)
                        .form("email", email)
                        .form("passwd", passwd)
                        .form("code", "")
                        .execute();
                if (loginRst == null) {
                    logger.log(Level.ERROR, "登录错误。");
                    continue;
                }

                if (loginRst.getStatus() != 200) {
                    logger.log(Level.ERROR, "登录失败。");
                    continue;
                }

                // checkin
                List<HttpCookie> cookies = loginRst.getCookies();

                HttpResponse checkinRst = HttpRequest.post(checkinUrl)
                        .header(Header.USER_AGENT, userAgent)
                        .cookie(cookies.toArray(new HttpCookie[0]))
                        .execute();

                if (checkinRst == null) {
                    logger.log(Level.ERROR, "签到错误。");
                    continue;
                }

                logger.log(Level.INFO, "签到信息：" + checkinRst.body());
                if (checkinRst.getStatus() != 200) {
                    System.out.println("签到失败。");
                    continue;
                }
                checked = true;
            } catch (IORuntimeException ioEx) {
                logger.log(Level.ERROR, ioEx.getMessage());
            } catch (InterruptedException e) {
                logger.log(Level.ERROR, "sleeping interrupt");
            }
        }
        if(checked) {
            logger.log(Level.INFO, "签到成功。");
        }else{
            if(notify){
                UrlTool.sendMessageViaServerChan(Const.SCKEY,"自动签到失败。","失败了失败了失败了");
            }
            logger.log(Level.ERROR, "自动签到失败");
        }
    }
}
