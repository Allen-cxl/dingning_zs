package com.dingning.cardzs.model;

import java.io.Serializable;

/**
 * Created by Allen on 2016/12/11.
 */

public class Message implements Serializable {

    private static final long serialVersionUID = 240012616283616026L;
    private String exhort_id;        //叮咛id
    private String parent_id;        //id
    private String content;          //叮咛内容
    private String add_time ;         //叮咛时间
    private String parent_pic;       //家长头像
    private String parent_relation;  //家长亲属关系
    private String state;              //读取状态 1-未读   2-已读
    public static final  int unRead = 1; //-未读
    public static final  int read  =2;    // 2-已读

    public String getExhort_id() {
        return exhort_id;
    }

    public void setExhort_id(String exhort_id) {
        this.exhort_id = exhort_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getParent_pic() {
        return parent_pic;
    }

    public void setParent_pic(String parent_pic) {
        this.parent_pic = parent_pic;
    }

    public String getParent_relation() {
        return parent_relation;
    }

    public void setParent_relation(String parent_relation) {
        this.parent_relation = parent_relation;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Message{" +
                "exhort_id='" + exhort_id + '\'' +
                ", parent_id='" + parent_id + '\'' +
                ", content='" + content + '\'' +
                ", add_time='" + add_time + '\'' +
                ", parent_pic='" + parent_pic + '\'' +
                ", parent_relation='" + parent_relation + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
