package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id")
  private User author;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "channel_id")
  private Channel channel;

  @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
  private final Set<MessageAttachment> attachments = new HashSet<>();

  private Message(
      String content,
      User author,
      Channel channel
  ) {
    this.content = content;
    this.author = author;
    this.channel = channel;
  }

  public static Message create(String content, User author, Channel channel) {
    return new Message(content, author, channel);
  }

  public void attach(BinaryContent binaryContent) {
    MessageAttachment attachment = MessageAttachment.attach(this, binaryContent);
    attachments.add(attachment);
  }

  public void assignIdForTest(UUID id) {
    this.id = id;
  }

  public void assignCreatedAtForTest(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public void updateContent(String content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Message message)) {
      return false;
    }
    return Objects.equals(id, message.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}