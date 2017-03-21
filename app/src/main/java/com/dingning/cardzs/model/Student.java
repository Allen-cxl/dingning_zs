package com.dingning.cardzs.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Allen on 2016/12/13.
 */

public class Student implements Serializable {

    private static final long serialVersionUID = 4274903160098137349L;
    private String student_name;
    private String student_id;

    private String class_name;
    private String class_id;
    private String student_pic;
    private ArrayList<Parent> parent;

    public String getStudent_name() {
        return student_name;
    }

    public ArrayList<Parent> getParent() {
        return parent;
    }

    public void setParent(ArrayList<Parent> parent) {
        this.parent = parent;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getStudent_pic() {
        return student_pic;
    }

    public void setStudent_pic(String student_pic) {
        this.student_pic = student_pic;
    }

    @Override
    public String toString() {
        return "Student{" +
                "student_name='" + student_name + '\'' +
                ", student_id='" + student_id + '\'' +
                ", class_name='" + class_name + '\'' +
                ", class_id='" + class_id + '\'' +
                ", student_pic='" + student_pic + '\'' +
                ", parent=" + parent +
                '}';
    }
}
