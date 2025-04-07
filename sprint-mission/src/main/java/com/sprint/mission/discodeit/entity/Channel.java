package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    // 필드 선언부
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String channelName;
    private List<UUID> messageList;

    // 생성자
    public Channel(String channelName) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.channelName = channelName;
        this.messageList = new ArrayList<>();
        this.updatedAt = createdAt;
    }

    // getter 함수부
    public String getChannelName() {
        return channelName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public UUID getId() {
        return id;
    }

    public List<UUID> getMessageList() {
        return messageList;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public UUID addMessageToChannel(UUID messageUUID){
        messageList.add(messageUUID);
        return messageUUID;
    }

    // channelName 업데이트 함수
    public void updateChannelName(String channelName){
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return  " 채널명 : " + channelName +
                " / 식별번호 : " + id +
                " / messageList : " + messageList
                +"\n";
    }
}
