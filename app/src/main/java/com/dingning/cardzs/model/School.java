package com.dingning.cardzs.model;

import java.io.Serializable;

/**
 * Created by Allen on 2017/1/12.
 */

public class School implements Serializable{

    private static final long serialVersionUID = -6153356658041007032L;
    private String class_name;
    private String school_name;
    private String school_logo;

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getSchool_logo() {
        return school_logo;
    }

    public void setSchool_logo(String school_logo) {
        this.school_logo = school_logo;
    }
}
