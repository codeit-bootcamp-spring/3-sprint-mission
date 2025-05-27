package com.sprint.mission.discodeit.entity.base;

import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate
  private Instant updatedAt;
}
