package com.dingning.cardzs.model;

import java.io.Serializable;

/**
 * Created by Allen on 2016/12/20.
 */

public class Version implements Serializable {

    public static final int UPDATE_FALSE = 0;  //现在不用更新
    public static final int UPDATE_TRUE= 1;  //现在必须更新
    private static final long serialVersionUID = 2162827176079719191L;

    private String version_name;
    private int version_code;
    private String content;
    private String url;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
