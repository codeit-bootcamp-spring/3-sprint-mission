package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  // Getter methods
  public UUID getId() {
    return id;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  // Protected setter for JPA
  protected void setId(UUID id) {
    this.id = id;
  }

  protected void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
