package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message { // 메시지 도메인 클래스 (작성자, 채널, 내용, 생성/수정 시간 등 정보 포함)
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

    // Getter 메서드
    public UUID getId() { // 메시지 ID 반환
        return id;
    }

    public long getCreatedAt() { // 생성 시간 반환
        return createdAt;
    }

    public long getUpdatedAt() { // 수정 시간 반환
        return updatedAt;
    }

    public UUID getUserId() { // 작성자 ID 반환
        return userId;
    }

    public UUID getChannelId() { // 채널 ID 반환
        return channelId;
    }

    public String getContent() { // 메시지 내용 반환
        return content;
    }

    // Update 메서드
    public void updateContent(String content) { // 메시지 내용 수정
        this.content = content;
        this.updatedAt = System.currentTimeMillis(); // 수정 시간 갱신
    }

    @Override
    public String toString() { // 메시지 객체를 문자열로 반환
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
