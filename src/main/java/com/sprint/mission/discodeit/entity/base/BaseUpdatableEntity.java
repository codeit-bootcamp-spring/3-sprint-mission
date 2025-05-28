package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@AllArgsConstructor

public class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public BaseUpdatableEntity() {
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "BaseUpdatableEntity{" +
                "updatedAt=" + updatedAt +
                '}';
    }

}