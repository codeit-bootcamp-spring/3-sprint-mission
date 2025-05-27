package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "messages", schema = "discodeit")
public class Message extends BaseUpdatableEntity {

  @Column(name = "content")
  private String content;

  @ManyToOne
  @JoinColumn(name = "channel_id")
  private Channel channel;

  @ManyToOne
  @JoinColumn(name = "author_id")
  private User author;

  @OneToMany
  private List<BinaryContent> attachmentIds;

  public Message() {
  }

  public Message(String content, Channel channel, User author) {
    this.content = content;
    this.channel = channel;
    this.author = author;
    this.attachmentIds = new ArrayList<>();
  }

  public void updateContent(String content) {
    this.content = content;
  }

  public void updateAttachmentIds(List<BinaryContent> attachmentIds) {
    this.attachmentIds = attachmentIds;
  }

//  public static MessageResponseDTO toDTO(Message message) {
//    MessageResponseDTO messageResponseDTO = new MessageResponseDTO(message.getId(),
//        message.getCreatedAt(),
//        message.getUpdatedAt(),
//        message.getContent(),
//        message.getAuthorId(),
//        message.getChannelId(),
//        message.getAttachmentIds());
//
//    return messageResponseDTO;
//  }

  @Override
  public String toString() {
    return "Message{" +
        "content='" + content + '\'' +
        "} " + super.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Message message = (Message) o;
    return Objects.equals(getId(), message.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
