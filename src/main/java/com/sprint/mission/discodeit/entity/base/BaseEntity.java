package com.sprint.mission.discodeit.entity.base;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;

public abstract class BaseEntity {

  UUID id;

  @CreatedDate
  Instant createdAt;
}
