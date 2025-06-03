package com.sprint.mission.discodeit.entity.base;

import java.time.Instant;
import lombok.Getter;

@Getter
public abstract class BaseUpdatableEntity extends BaseEntity{
    protected Instant updatedAt;
}
