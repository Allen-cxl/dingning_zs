package com.dingning.cardzs.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Allen on 2016/12/13.
 */

public class Parent implements Serializable {


    private static final long serialVersionUID = 1054795621156757149L;
    private String parent_id;
    private String parent_name;
    private String relation;
    private String parent_pic;
    private boolean isSelected;

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getParent_pic() {
        return parent_pic;
    }

    public void setParent_pic(String parent_pic) {
        this.parent_pic = parent_pic;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "Parent{" +
                "parent_id='" + parent_id + '\'' +
                ", parent_name='" + parent_name + '\'' +
                ", relation='" + relation + '\'' +
                ", parent_pic='" + parent_pic + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
