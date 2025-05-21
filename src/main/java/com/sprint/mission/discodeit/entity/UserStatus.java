package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
  private static final long serialVersionUID = 1L;
  private final UUID id; // UserStatus의 고유ID
  private final Instant createdAt;
  private Instant updatedAt;

  private UUID userId; // 온/오프 상태에 속한 사용자ID - User도메인

  public UserStatus(UUID userId) {
    this.id = UUID.randomUUID();
    this.createdAt =Instant.now() ;
    this.updatedAt = Instant.now();
    this.userId = userId;
  }

  // 사용자가 로그인 -> 서비스에서 호출
  public void update(Instant newLastUpdatedAt) {
    this.updatedAt = newLastUpdatedAt;
  }

  /*
  사용자가 로그인을 하면 마지막 접속 시간은 로그인을 한 시간이고 5분간 online
  updateAt을 마지막 로그인 시간으로 가정
  별도의 로그아웃 기능이 없으므로 현재 시간으로부터 5분이 지나면 offline 상태로 표시
   */
  public boolean checkUserConnect() {
    return updatedAt.plusSeconds(300).isAfter( Instant.now());
  }
}

