package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 이 클래스는 공통 필드를 상속시키기 위한 부모 클래스이다.
 */
@Getter
public class BaseEntity {

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt =  System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public void setUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
}
