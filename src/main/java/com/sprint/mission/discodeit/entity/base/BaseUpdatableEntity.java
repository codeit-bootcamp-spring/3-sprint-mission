package com.sprint.mission.discodeit.entity.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class BaseUpdatableEntity extends BaseEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @LastModifiedDate
    private Instant updatedAt;
}
