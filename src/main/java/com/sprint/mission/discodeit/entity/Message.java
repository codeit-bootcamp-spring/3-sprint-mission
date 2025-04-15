package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id = UUID.randomUUID();
    private long createdAt  = System.currentTimeMillis();
    private long updatedAt;

    private int msgNumber;
    private String author;
    private String textMsg;


    public Message(int msgNumber, String author, String textMsg,long createdAt, long updatedAt) {
        this.msgNumber = msgNumber;
        this.author = author;
        this.textMsg = textMsg;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public UUID getId() {
        return id;
    }
    public int getMsgNumber() {
        return msgNumber;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String newAuthor) {
        this.author = newAuthor;
    }
    public void setTextMsg(String textMsg) {
        this.textMsg = textMsg;
    }
    public String getTextMsg() {
        return textMsg;
    }

    public String getCreatedAt() {
        String formatedTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(createdAt);
        return formatedTime;
    }
    public String getUpdatedAt() {
        String formatedTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(updatedAt);
        return formatedTime;
    }
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    @Override
    public String toString() {
        String result;
        if(getCreatedAt().equals(getUpdatedAt())){
            result = msgNumber + "    | " + author + " : " + textMsg + "  (" + getCreatedAt() + ") : " + getId();
        } else {
            result = msgNumber + "    | " + author + " : " + textMsg + "  (" + getCreatedAt() + " / " + getUpdatedAt() + ") : " + getId();
        }
        return result;
    }
}

