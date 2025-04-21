package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable { // 직렬화를 위해 Serializable 구현
    private static final long serialVersionUID = 1L; // 클래스 버전 식별자

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

    // 사용자 이름, 수정 시간 함께 업데이트
    public void updateName(String name) {
        this.name = name;                                   // 사용자 이름 업데이트
        this.updatedAt = System.currentTimeMillis();        // 수정 시간 업데이트
    }

    // 사용자 이름만 업데이트
    public void setName(String newName) {
        this.name = newName;
    }

    // 수정 시간만 변경하고 다른 필드는 그대로 유지
    public void updateUpdatedAt() { // 수정되었을 때 '마지막 수정 시간'을 갱신
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

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}