package com.sprint.mission.discodeit.entity.base;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class BaseEntity {
    private UUID id;
    private Instant createdAt;
}
