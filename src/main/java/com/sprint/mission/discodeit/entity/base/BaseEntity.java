package com.sprint.mission.discodeit.entity.base;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  protected UUID id;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  protected Instant createdAt;
}
