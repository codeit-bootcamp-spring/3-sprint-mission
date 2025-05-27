package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Channel extends BaseUpdatableEntity {

  private String name;
  private String description;
  private ChannelType type;

  public Channel(String name, String description) {
    this.name = name;
    this.description = description;
    this.type = ChannelType.PUBLIC;
  }

  public Channel(List<UUID> participantIds) {
    this.type = ChannelType.PRIVATE;
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateDescription(String description) {
    this.description = description;
  }

//  public static ChannelResponseDTO toDTO(Channel channel) {
//    ChannelResponseDTO channelResponseDTO = new ChannelResponseDTO(
//        channel.getId(),
//        channel.getCreatedAt(),
//        channel.getUpdatedAt(),
//        channel.getName(),
//        channel.getDescription(),
//        channel.getType(),
//        channel.getParticipantIds(),
//        channel.getMessages(),
//        channel.getLastMessageTime()
//    );
//
//    return channelResponseDTO;
//  }


  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Channel channel = (Channel) o;
    return Objects.equals(getId(), channel.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
