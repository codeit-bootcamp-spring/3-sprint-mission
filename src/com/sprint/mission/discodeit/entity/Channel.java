package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.jcf.JCFMessageService;

import java.text.SimpleDateFormat;
import java.util.UUID;


public class Channel {
    private final UUID id = UUID.randomUUID();
    private long createdAt  = System.currentTimeMillis();
    private long updatedAt;
    private String channelName;
    private String channelDescription;
    private String channelCreater;

    private JCFMessageService messageService;


    public Channel(String channelName, String channelDescription, String channelCreater, long createdAt, long updatedAt) {
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.channelCreater = channelCreater;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.messageService = new JCFMessageService();
    }

    public UUID getId() {return id;}
    public String getCreatedAt() {
        String formatedTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(createdAt);
        return formatedTime;}

    public String getUpdatedAt() {
        String formatedTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(updatedAt);
        return formatedTime;}
    public void setUpdatedAt(long updatedAt) {this.updatedAt = updatedAt;}

    public String getChannelName() {return channelName;}
    public void setChannelName(String channelName) {this.channelName = channelName;}

    public String getChannelDescription() {return channelDescription;}
    public void setChannelDescription(String channelDescription) {this.channelDescription = channelDescription;}

    public String getChannelCreater() {return channelCreater;}

    public JCFMessageService messageService() {return messageService;}
    @Override
    public String toString() {
        String result;
        if(getCreatedAt().equals(getUpdatedAt())){
            result = "채널명 : " + channelName + "   개설자 : " + channelCreater + "   채널설명 : " + channelDescription + "   개설일자 : " + getCreatedAt() + "   UUID : " + getId();
        } else {
            result = "채널명 : " + channelName + "   개설자 : " + channelCreater + "   채널설명 : " + channelDescription + "   개설일자 : " + getCreatedAt() + " 수정일자 : " + getUpdatedAt() + "   UUID : " + getId();
        }
        return result;
    }
}

