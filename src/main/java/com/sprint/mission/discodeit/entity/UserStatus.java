package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatus implements Serializable {

  private static final Long serialVersionUID = 1L;
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  //
  private final UUID userId;
  //
  private UserStatusType status;
  private Instant lastActiveAt;

  public UserStatus(UUID userId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    this.status = UserStatusType.ONLINE;
    this.lastActiveAt = Instant.now();
    //
    this.userId = userId;
  }

//  public void update(UserStatusType newStatus) {
//    boolean anyValueUpdated = false;
//    if (newStatus != null && newStatus != this.status) {
//      // 이전 상태가 온라인이였고 현재가 온라인이 아닐때, 바뀌는 시점에 lastActiveAt를 현재시간으로 업데이트 해줘야함.
//      if (this.status.equals(UserStatusType.ONLINE)) {
//        this.lastActiveAt = Instant.now();
//      }
//
//      this.status = newStatus;
//
//      anyValueUpdated = true;
//    }
//
//    if (anyValueUpdated) {
//      this.updatedAt = Instant.now();
//    }
//  }

  public void update(Instant newLastActiveAt) {
    boolean anyValueUpdated = false;

    if (newLastActiveAt != null && this.lastActiveAt != newLastActiveAt) {
      this.lastActiveAt = newLastActiveAt;
      anyValueUpdated = true;
    }

    /*deprecated field (newStatus) */
//    if (newStatus != null && newStatus != this.status) {
//      // 이전 상태가 온라인이였고 현재가 온라인이 아닐때, 바뀌는 시점에 lastActiveAt를 현재시간으로 업데이트 해줘야함.
//      if (this.status.equals(UserStatusType.ONLINE)) {
//        this.lastActiveAt = Instant.now();
//      }
//
//      this.status = newStatus;
//
//      anyValueUpdated = true;
//    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }


  //lastActiveAt 값이 5분 이내라면 온라인 유저로 간주
  public boolean isOnline() {
    Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }

  @Override
  public String toString() {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault());

    String createdAtFormatted = formatter.format(createdAt);
    String updatedAtFormatted = formatter.format(updatedAt);

    return "🙋‍♂️ UserStatus {\n" +
        "  id         = " + id + "\n" +
        "  createdAt  = " + createdAtFormatted + "\n" +
        "  updatedAt  = " + updatedAtFormatted + "\n" +
        "  userId       = " + userId + "\n" +
        "  status       = " + status + "\n" +
        "  lastActiveAt       = " + lastActiveAt + "\n" +
        "}";
  }
}
