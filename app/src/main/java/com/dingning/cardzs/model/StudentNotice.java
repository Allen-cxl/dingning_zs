package com.dingning.cardzs.model;

import java.io.Serializable;

/**
 * Created by Allen on 2016/12/15.
 */

public class StudentNotice implements Serializable {

    private static final long serialVersionUID = 2118394754468645777L;
    private String student_id;
    private String rid;
    private String type;
    private String message;

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "StudentNotice{" +
                "student_id='" + student_id + '\'' +
                ", rid='" + rid + '\'' +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
