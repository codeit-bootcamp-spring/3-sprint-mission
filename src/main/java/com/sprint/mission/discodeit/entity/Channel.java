package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private ChannelType type;

  @Column(name = "name", length = 100)
  private String name;

  @Column(name = "description", length = 500)
  private String description;

  private Channel(ChannelType type) {
    this.name = null;
    this.description = null;
    this.type = type != null ? type : ChannelType.PUBLIC;
  }

  private Channel(String name, String description, ChannelType type) {
    this.name = Objects.requireNonNull(name);
    this.description = description != null ? description : "";
    this.type = type;
  }

  public static Channel createPublic(String name, String description) {
    return new Channel(name, description, ChannelType.PUBLIC);
  }

  public static Channel createPrivate() {
    return new Channel(ChannelType.PRIVATE);
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateDescription(String description) {
    this.description = description;
  }

  public boolean isPublic() {
    return this.type == ChannelType.PUBLIC;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Channel channel)) {
      return false;
    }
    return Objects.equals(id, channel.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
