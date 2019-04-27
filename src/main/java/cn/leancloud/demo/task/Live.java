package cn.leancloud.demo.task;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

@AVClassName("Live")
public class Live extends AVObject {
    public String getTitle(){
        return getString("title");
    }

    public int getStatus(){
        return getInt("status");
    }
}
