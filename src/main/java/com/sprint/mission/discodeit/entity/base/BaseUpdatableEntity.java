package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedDate;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
