package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor
public class Message extends BaseUpdateableEntity {

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  // Message → Channel (ON DELETE CASCADE이지만 자식이므로 cascade 설정 없음)
  @ManyToOne // Message-Channel = N:1
  @JoinColumn(name = "channel_id", nullable = false) // Foreign Key
  private Channel channel;

  // Message → User (ON DELETE SET NULL, 독립적 관계이므로 cascade 설정 없음)
  @ManyToOne // Message-User = N:1
  @JoinColumn(name = "author_id") // Foreign Key
  private User author;

  // Message → MessageAttachment 양방향 관계 (부모 → 자식)
  @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MessageAttachment> messageAttachments = new ArrayList<>();

  public Message(String content, Channel channel, User author) {
    this.content = content;
    this.channel = channel;
    this.author = author;
  }

  // BinaryContent 리스트를 받아 MessageAttachment들을 자동 생성하는 생성자
  public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
    this.content = content;
    this.channel = channel;
    this.author = author;
    this.messageAttachments = new ArrayList<>();

    // BinaryContent들을 MessageAttachment로 변환하여 추가
    if (attachments != null) {
      for (BinaryContent attachment : attachments) {
        MessageAttachment messageAttachment = new MessageAttachment(this, attachment);
        this.messageAttachments.add(messageAttachment);
      }
    }
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }
  }
}
