package com.sprint.mission.discodeit.Dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.channel fileName       : ChannelCreateResponse
 * author         : doungukkim date           : 2025. 4. 29. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 29.        doungukkim 최초 생성
 */
@Getter
public class ChannelCreateResponse {

  private final UUID id;
  private final Instant createdAt;
  private final Instant updatedAt;
  private final ChannelType type;
  private final String name;
  private final String description;


  public ChannelCreateResponse(UUID id, Instant createdAt, Instant updatedAt, ChannelType type) {
    this.id = id;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.type = type;
    this.name ="";
    this.description = "";
  }

  public ChannelCreateResponse(UUID id, Instant createdAt, Instant updatedAt, ChannelType type,
      String name, String description) {
    this.id = id;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.type = type;
    this.name = name;
    this.description = description;
  }
}
