package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User { // 사용자 도메인 클래스 (고유 ID, 이름, 생성/수정 시간 포함)
    private final UUID id; // 고유 사용자 ID
    private final long createdAt; // 생성 시간
    private long updatedAt; // 마지막 수정 시간
    private String name; // 사용자 이름

    // 생성자: 사용자 생성 시 ID 및 시간 자동 설정
    public User(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;
    }

    // Getter 메서드
    public UUID getId() { // 사용자 ID 반환
        return id;
    }

    public long getCreatedAt() { // 생성 시간 반환
        return createdAt;
    }

    public long getUpdatedAt() { // 수정 시간 반환
        return updatedAt;
    }

    public String getName() { // 사용자 이름 반환
        return name;
    }

    // Update 메서드
    public void updateName(String name) { // 사용자 이름 수정
        this.name = name;
        this.updatedAt = System.currentTimeMillis(); // 수정 시간 갱신
    }

    @Override
    public String toString() { // 사용자 객체를 문자열로 반환
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}