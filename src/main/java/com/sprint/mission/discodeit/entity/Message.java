package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable { // 직렬화를 위해 Serializable 구현
    private static final long serialVersionUID = 1L; // 클래스 버전

    private final UUID id; // 고유 메시지 ID
    private final long createdAt; // 메시지 생성 시간
    private long updatedAt; // 메시지 마지막 수정 시간

    private final UUID userId; // 작성자 ID (User와 연관)
    private final UUID channelId; // 채널 ID (Channel과 연관)
    private String content; // 메시지 내용

    // 생성자: 메시지 생성 시 ID, 시간 자동 설정
    public Message(UUID userId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    public void setContent(String newContent) { // 메시지 내용 업데이트
        this.content = newContent;
    }

    public void updateUpdatedAt() { // 메시지 수정 시간 업데이트
        this.updatedAt = System.currentTimeMillis();
    }


    // Getter 메서드
    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
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

    // 메시지 내용 수정
    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}