package com.sprint.mission.discodeit.Dto.channel;

import com.sprint.mission.discodeit.Dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
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
  private final ChannelType type;
  private final String name;
  private final String description;
  private final List<JpaUserResponse> participants;
  private final Instant lastMessageAt;

  public ChannelCreateResponse(UUID id, ChannelType type, List<JpaUserResponse> participants, Instant lastMessageAt) {
    this.id = id;
    this.type = type;
    this.name ="";
    this.description = "";
    this.participants = participants;
    this.lastMessageAt = lastMessageAt;
  }

  public ChannelCreateResponse(UUID id, ChannelType type, String name, String description,List<JpaUserResponse> participants, Instant lastMessageAt) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.description = description;
    this.participants = participants;
    this.lastMessageAt = lastMessageAt;
  }
}
