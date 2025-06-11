package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@MappedSuperclass
@Getter
@RequiredArgsConstructor
public abstract class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate
    Instant updatedAt;
}
