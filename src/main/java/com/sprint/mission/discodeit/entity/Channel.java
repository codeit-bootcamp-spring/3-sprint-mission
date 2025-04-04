package com.sprint.mission.discodeit.entity;


import java.util.UUID; // UUID(고유 ID) 생성을 위한 기능 가져오기

public class Channel {
    private final UUID id; // 채널의 고유한 ID
    private final Long createdAt; //채널이 만들어진 시각
    private Long updatedAt; // 채널 정보가 업데이트된 시각
    private String name; // 채널의 이름

    //===========================================
    // 생성자 (Channel을 만들 때 실행되는 부분)
    //===========================================
    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.name = name;
    }

    //===========================================
    // Getter 매서드들 (데이터를 꺼내볼 수 있게 하는 함수)
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
    // 이름을 바꿀 때 사용하는 매서드 (수정 기능)
    //===========================================
    public void updateName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }
}
