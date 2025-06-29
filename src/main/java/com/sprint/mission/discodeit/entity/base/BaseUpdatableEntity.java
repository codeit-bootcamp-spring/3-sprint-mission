package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;

import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "timestamp with time zone")
    private Instant updatedAt;
}
