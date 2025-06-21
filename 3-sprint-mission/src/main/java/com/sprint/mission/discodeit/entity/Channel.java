package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "channels", schema = "discodeit")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private ChannelType type;
  @Column(name = "name")
  private String name;
  @Column(name = "description")
  private String description;

  @Builder
  public Channel(String name, String description, ChannelType type) {
    this.name = name;
    this.description = description;
    this.type = type;
  }

  public void update(String newName, String newDescripton) {
    boolean anyValueUpdated = false;
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
      anyValueUpdated = true;
    }

    if (newDescripton != null && !newDescripton.equals(this.description)) {
      this.description = newDescripton;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }

}
