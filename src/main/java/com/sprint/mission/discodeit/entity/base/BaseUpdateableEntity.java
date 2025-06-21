package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@MappedSuperclass
public abstract class BaseUpdateableEntity extends BaseEntity {

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  // Getter method
  public Instant getUpdatedAt() {
    return updatedAt;
  }

  // Protected setter for JPA
  protected void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}