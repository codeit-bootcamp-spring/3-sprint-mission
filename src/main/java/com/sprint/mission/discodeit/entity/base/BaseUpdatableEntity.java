package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    protected void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }
}