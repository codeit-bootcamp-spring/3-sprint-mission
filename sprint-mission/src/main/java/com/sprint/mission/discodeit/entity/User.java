package com.sprint.mission.discodeit.entity;


import java.util.UUID;

public class User {
    // 필드 선언부
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String userName;

    // 생성자
    public User(String userName) {
        // 버전 4로 랜덤한 UUID 생성
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.userName = userName;
    }

    // getter 함수부
    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getUserName() {
        return userName;
    }

    // userName 업데이트 함수
    public void updateUserName(String userName) {
        this.userName = userName;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return  " 이름 : " + userName + " /" +
                " 아이디 생성 시간 : " + createdAt + " /" +
                " 업데이트 된 시간 : " + updatedAt + " /" +
                " 식별번호 = " + id + "\n";
    }
}
