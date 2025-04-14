package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id; // 메시지 고유 아이디
    private final Long createdAt; // 메시지 생성 시각
    private Long updatedAt; // 메시지 업데이트 시각

    private final UUID userId; // 보낸 사람의 아이디
    private final UUID channelId; // 메시지를 보낸 채널의 아이디
    private String content; // 메시지 내용

    //===========================================
    // 생성자 (메시지 객체가 만들어질 때 실행됨)
    //===========================================
    public Message(UUID userId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    //===========================================
    // Getter 매서드 (각 데이터를 꺼내볼 수 있게 하는 함수)
    //===========================================
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
    }

    //===========================================
    // 메시지 내용을 수정할 때 사용하는 매서드
    //===========================================
    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}
