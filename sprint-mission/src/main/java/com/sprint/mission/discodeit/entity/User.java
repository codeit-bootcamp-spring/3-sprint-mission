package com.sprint.mission.discodeit.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User implements Serializable {
    // 필드 선언부
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String userName;
    private final List<UUID> channelList;

    // 생성자
    public User(String userName) {
        // 버전 4로 랜덤한 UUID 생성
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.userName = userName;
        this.channelList = new ArrayList<>();
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

    public List<UUID> getChannelList() { return channelList; }

    // userName 업데이트 함수
    public void updateUserName(String userName) {
        this.userName = userName;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return  " 이름 : " + userName + " / 유저 식별 ID : " + id  + " / 유저가 포함된 채널 식별 ID : " + channelList
                +  "\n";
    }
}
