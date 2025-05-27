package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate
  @Column(name = "updated_at")
  protected Instant updatedAt;
}
