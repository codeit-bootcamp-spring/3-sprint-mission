package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

import java.io.Serializable;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  // 필드 정의
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 10)
  private ChannelType type;

  @Column(name = "name", nullable = true, length = 100)
  private String name;

  @Column(name = "description", nullable = true, length = 500)
  private String description;

  @OneToMany(mappedBy = "channel", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ReadStatus> readStatuses;

  @OneToMany(mappedBy = "channel", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Message> messages;

  // 생성자
  public Channel(ChannelType type, String name, String description) {
    this.type = type;
    this.name = name;
    this.description = description;
    this.readStatuses = new ArrayList<>();
    this.messages = new ArrayList<>();
  }

  // Update
  public void update(String newName, String newDescription) {
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
    }
  }
}
