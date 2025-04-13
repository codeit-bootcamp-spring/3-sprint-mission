package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel { // 채널 도메인 클래스 (생성 시간, 수정 시간, 이름 등 정보 포함)
    private final UUID id; // 고유 식별자
    private final long createdAt; // 생성 시간
    private long updatedAt; // 마지막 수정 시간
    private String name; // 채널 이름

    // 생성자 부분, id, creatAt, updateAt 자동 초기화

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;
    }

    // Getter 메서드
    public UUID getId() { // 채널 ID 반환
        return id;
    }

    public long getCreatedAt() { // 생성 시간 반환
        return createdAt;
    }

    public long getUpdatedAt() { // 수정 시간 반환
        return updatedAt;
    }

    public String getName() { // 채널 이름 반환
        return name;
    }

    // Update 메서드
    public void updateName(String name) { // 채널 이름 변경 메서드
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override // 채널 객체를 문자열로 반환
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
