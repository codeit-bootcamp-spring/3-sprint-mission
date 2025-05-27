package com.sprint.mission.discodeit.entity.base;

import java.time.Instant;
import org.springframework.data.annotation.LastModifiedDate;

public class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate
  Instant updatedAt;
}
