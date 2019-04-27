package cn.leancloud.demo.task;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

@AVClassName("Todo")
public class Source extends AVObject {

    public String getApiLink() {
        return getString("apiLink");
    }

    public String getSourceName() {
        return getString("sourceName");
    }

    public boolean getState() {
        return getBoolean("state");
    }

    public int getCategoryId(){
        return getInt("categoryId");
    }

}
