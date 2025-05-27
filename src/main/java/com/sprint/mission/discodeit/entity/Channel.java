package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDTO;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Channel extends BaseUpdatableEntity {

  private String name;
  private UUID channelMaster;
  private String description;
  private ChannelType type;
  private final List<UUID> participantIds;
  private final List<UUID> messages;
  private Instant lastMessageTime;

  public Channel(String name, UUID channelMaster, String description) {
    this.name = name;
    this.channelMaster = channelMaster;
    this.description = description;
    this.type = ChannelType.PUBLIC;
    this.participantIds = new ArrayList<>();
    this.messages = new ArrayList<>();
  }

  public Channel(UUID channelMaster, List<UUID> participantIds) {
    this.name = "";
    this.channelMaster = channelMaster;
    this.description = "";
    this.type = ChannelType.PRIVATE;
    this.participantIds = participantIds;
    this.messages = new ArrayList<>();
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateDescription(String description) {
    this.description = description;
  }

  public void updateLastMessageTime(Instant lastMessageTime) {
    this.lastMessageTime = lastMessageTime;
  }

  public static ChannelResponseDTO toDTO(Channel channel) {
    ChannelResponseDTO channelResponseDTO = new ChannelResponseDTO(
        channel.getId(),
        channel.getCreatedAt(),
        channel.getUpdatedAt(),
        channel.getName(),
        channel.getChannelMaster(),
        channel.getDescription(),
        channel.getType(),
        channel.getParticipantIds(),
        channel.getMessages(),
        channel.getLastMessageTime()
    );

    return channelResponseDTO;
  }

  @Override
  public String toString() {
    return "Channel {\n" +
        "  id=" + getId() + ",\n" +
        "  createdAt=" + getCreatedAt() + ",\n" +
        "  updatedAt=" + getUpdatedAt() + ",\n" +
        "  name='" + name + "',\n" +
        "  channelMaster='" + channelMaster + "', \n" +
        "  description='" + description + "',\n" +
        "  users=" + participantIds.stream().toList() + ",\n" +
        "  messages=" + messages.stream().toList() + "\n" +
        '}';
  }

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
