package com.springboot.netty.model;

public class Message {

    private Message(){}

    public static Message build(){
        return new Message();
    }

    private long id;

    private String msg;

    public long getId() {
        return id;
    }

    public Message setId(long id) {
        this.id = id;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Message setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
