package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ReadStatus implements Serializable {

  private static final Long serialVersionUID = 1L;
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  //
  private final UUID userId;
  private final UUID channelId;
  //
  // XXX. isRead 필요없음 lastReadAt로 처리하면 됨
  private Instant lastReadAt;


  // XXX. 처음에 생성될때 읽지않은 상태가 초기값이 맞나? -> YES
  public ReadStatus(UUID userId, UUID channelId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    this.lastReadAt = null;

    //
    this.userId = userId;
    this.channelId = channelId;
  }

  public void update(Instant lastReadAt) {
    boolean anyValueUpdated = false;
    if (lastReadAt != null && !lastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = lastReadAt;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();

    }
  }

  @Override
  public String toString() {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault());

    String createdAtFormatted = formatter.format(createdAt);
    String updatedAtFormatted = formatter.format(updatedAt);

    return "🙋‍♂️ ReadStatus {\n" +
        "  id         = " + id + "\n" +
        "  createdAt  = " + createdAtFormatted + "\n" +
        "  updatedAt  = " + updatedAtFormatted + "\n" +
        "  userId       = " + userId + "\n" +
        "  channelId       = " + channelId + "\n" +
        "  lastReadAt       = " + lastReadAt + "\n" +
        "}";
  }
}
