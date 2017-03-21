package com.dingning.cardzs.model;

import java.io.Serializable;

/**
 * Created by Allen on 2016/12/14.
 */

public class Image implements Serializable {

    private static final long serialVersionUID = 4376952316024520486L;

    public String getImg_url() {
        return img_url;
    }

    @Override
    public String toString() {
        return "Image{" +
                "img_url='" + img_url + '\'' +
                '}';
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    private String img_url;
}
