package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

/**
 * PackageName  : com.sprint.mission.discodeit.entity
 * FileName     : BaseUpdatableEntity
 * Author       : dounguk
 * Date         : 2025. 5. 27.
 */
@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseUpdatableEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @LastModifiedDate
    @Column(name = "updated_at")
    protected Instant updatedAt;

}
