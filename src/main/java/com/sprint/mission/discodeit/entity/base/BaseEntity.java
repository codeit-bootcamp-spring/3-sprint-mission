package com.sprint.mission.discodeit.entity.base;

import java.io.Serializable;
import java.util.UUID;

public abstract class BaseEntity implements Serializable {
    protected UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}