package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable { // 직렬화 인터페이스 구현
    private static final long serialVersionUID = 1L; // 클래스 버전 식별자

    private final UUID id; // 고유 식별자
    private final long createdAt; // 생성 시간
    private long updatedAt; // 마지막 수정 시간
    private String name; // 채널 이름

    // 생성자: id, createdAt, updatedAt 자동 초기화
    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;
    }
    public void setName(String newName) { // 이름을 새로운 값으로 업데이트
        this.name = newName;
    }

    public void updateUpdatedAt() {
        this.updatedAt = System.currentTimeMillis(); // 수정 시간을 현재 시간으로 업데이트
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

    public String getName() {
        return name;
    }

    // Update 메서드
    public void updateName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}