package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
@Table(name = "channels", schema = "discodeit")
public class Channel extends BaseUpdatableEntity {

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private ChannelType type;

  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<ReadStatus> readStatuses;

  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Message> messages;

  public Channel() {
  }

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
