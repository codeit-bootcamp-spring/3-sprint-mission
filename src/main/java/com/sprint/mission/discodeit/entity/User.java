package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID id; // 유저의 고유한 ID
    private final Long createdAt; // 유저가 생성된 시각
    private Long updatedAt; // 유저 정보 업데이트 시각
    private String name; // 유저 이름

    //===========================================
    // 생성자 (유저를 만들 때 실행되는 부분)
    //===========================================
    public User(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.name = name;
    }

    //===========================================
    // Getter 매서드들(데이터를 꺼내볼 수 있게 하는 함수)
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

    public String getName() {
        return name;
    }

    //===========================================
    // 유저 이름을 수정하는 매서드
    //===========================================
    public void updateName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }
}


