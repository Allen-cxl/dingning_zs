package com.dingning.cardzs.model;

import java.io.Serializable;

/**
 * Created by victor on 2016/10/14.
 */

public class StringEvent implements Serializable {

    private static final long serialVersionUID = 3879369705663385128L;
    public String str;

    public StringEvent(String str) {
        this.str = str;
    }
}
