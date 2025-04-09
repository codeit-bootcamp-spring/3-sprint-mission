package com.sprint.mission.discodeit.entity;


import java.util.UUID;

public class User {
    // 필드 선언부
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String userName;
    private final UUID channelId;

    // 생성자
    public User(String userName, UUID channelId) {
        // 버전 4로 랜덤한 UUID 생성
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.userName = userName;
        this.channelId = channelId;
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

    public UUID getChannelId() { return channelId; }

    // userName 업데이트 함수
    public void updateUserName(String userName) {
        this.userName = userName;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return  " 이름 : " + userName + " / 유저 식별 ID : " + id  + " / 유저가 포함된 채널 식별 ID : " + channelId
                +  "\n";
    }
}
