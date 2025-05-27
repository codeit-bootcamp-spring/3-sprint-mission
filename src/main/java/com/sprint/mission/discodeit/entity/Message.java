package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "messages")
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseUpdatableEntity {

  @Column(name = "content")
  private String content;

  @ManyToOne
  @JoinColumn(name = "channel_id")
  private Channel channel;

  @ManyToOne
  @JoinColumn(name = "author_id")
  private User author;

  @Column(name = "attachments")
  private List<BinaryContent> attachments;


  public void update(String newContent) {
    boolean anyValueUpdated = false;
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}
