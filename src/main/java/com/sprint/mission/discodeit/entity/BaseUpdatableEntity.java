package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

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
@NoArgsConstructor
public abstract class BaseUpdatableEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @LastModifiedDate
    protected Instant updatedAt;

}
