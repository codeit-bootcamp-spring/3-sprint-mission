package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
public class Message extends BaseUpdatableEntity {

  private String content;
  private Channel channel;
  private User author;
  private List<BinaryContent> attachmentIds;

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
