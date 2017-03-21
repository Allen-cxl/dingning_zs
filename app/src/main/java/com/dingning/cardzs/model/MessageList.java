package com.dingning.cardzs.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Allen on 2016/12/16.
 */

public class MessageList implements Serializable{

    private static final long serialVersionUID = 8312852756698697557L;
    private String total;
    private List<Message> messages;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
