package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor
public class Message extends BaseUpdateableEntity {

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @ManyToOne // Message-Channel = N:1
  @JoinColumn(name = "channel_id", nullable = false) // Foreign Key
  private Channel channel;

  @ManyToOne // Message-User = N:1
  @JoinColumn(name = "author_id") // Foreign Key
  private User author;

  @ManyToMany // Message-BinaryContent = N:M
  @JoinTable(name = "message_attachments", joinColumns = @JoinColumn(name = "message_id"), inverseJoinColumns = @JoinColumn(name = "attachment_id")) // 중간테이블
  private List<BinaryContent> attachments;

  public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
    this.content = content;
    this.channel = channel;
    this.author = author;
    this.attachments = attachments;
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }
  }
}
