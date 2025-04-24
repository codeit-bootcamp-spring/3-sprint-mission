package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.entity
 * fileName       : BaseEntity
 * author         : doungukkim
 * date           : 2025. 4. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 23.        doungukkim       최초 생성
 */
@Getter
public abstract class BaseEntity implements Serializable {
    protected Instant createdAt;
    @Setter
    protected Instant updatedAt;
    protected UUID id;


    public BaseEntity() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.id = UUID.randomUUID();
    }
}
