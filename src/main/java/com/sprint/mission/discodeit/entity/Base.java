package com.sprint.mission.discodeit.entity;

import java.util.UUID;

/**
 * 공통 필드
 * <ul>
 *   <li>id 엔티티의 고유 식별자 (생성 시 초기화, 변경 불가)</li>
 *   <li>createdAt 엔티티 생성 시간 (생성 시 초기화, 변경 불가)</li>
 *   <li>updatedAt 마지막 수정 시간 (생성 시 초기화, 수정 가능)</li>
 * </ul>
 */
public class Base {

  private final UUID id;
  private final long createdAt;
  private long updatedAt;

  public Base() {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
  }

  public UUID getId() {
    return id;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt() {
    this.updatedAt = System.currentTimeMillis();
  }
}
