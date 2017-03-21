package com.dingning.cardzs.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Allen on 2016/12/14.
 */

public class MessageDetail implements Serializable {

    private static final long serialVersionUID = 7393437731398593987L;
    private String content;     //叮咛内容
    private String voice_url;   //语音url
    private String voice_time;  //语音时间
    private String video_url;   //视频url
    private String add_time;    //添加时间
    private ArrayList<Image> imgs;   //图片

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVoice_url() {
        return voice_url;
    }

    public void setVoice_url(String voice_url) {
        this.voice_url = voice_url;
    }

    public String getVoice_time() {
        return voice_time;
    }

    public void setVoice_time(String voice_time) {
        this.voice_time = voice_time;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public ArrayList<Image> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<Image> imgs) {
        this.imgs = imgs;
    }

    @Override
    public String toString() {
        return "MessageDetail{" +
                "content='" + content + '\'' +
                ", voice_url='" + voice_url + '\'' +
                ", voice_time='" + voice_time + '\'' +
                ", video_url='" + video_url + '\'' +
                ", add_time='" + add_time + '\'' +
                ", imgs=" + imgs +
                '}';
    }
}
