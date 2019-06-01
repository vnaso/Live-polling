package cn.leancloud.demo.task;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * IpProxy
 *
 * @author vnaso
 * @date 2019/6/1
 */
@AVClassName("IpProxy")
public class IpProxy extends AVObject {

    public String getProxyList(){
        return getString("proxyList");
    }
}
