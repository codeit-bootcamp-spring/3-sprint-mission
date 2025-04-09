package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    // 필드 선언부
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String text;
    private final UUID channelId; // 메시지를 보낼 채널의 id
    private final UUID userId; // 메시지 보낸 유저의 Id
    // 생성자
    public Message(String text, UUID channelId, UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.text = text;
        this.channelId = channelId;
        this.userId = userId;
    }

    // getter 함수부
    public long getCreatedAt() {
        return createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return  "  Messsage : " + text +
                " ( 보낸 시간 : " + updatedAt + " )"  + "/  보내진 메시지의 채널 식별 ID : " + channelId
                + " / 메시지를 보낸 유저의 식별 ID : " + userId
                + "\n";
    }

    // text 업데이트 함수
    public void updateText(String text) {
        this.text = text;
        this.updatedAt = System.currentTimeMillis();
    }
}
