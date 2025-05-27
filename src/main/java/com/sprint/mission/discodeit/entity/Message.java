package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Message extends BaseUpdatableEntity {

  private String content;
  private final UUID authorId;
  private final UUID channelId;
  private List<UUID> attachmentIds;

  public Message(UUID authorId, UUID channelId, String content) {
    this.content = content;
    this.authorId = authorId;
    this.channelId = channelId;
    this.attachmentIds = new ArrayList<>();
  }

  public void updateContent(String content) {
    this.content = content;
  }

  public void updateAttachmentIds(List<UUID> attachmentIds) {
    this.attachmentIds = attachmentIds;
  }

  public static MessageResponseDTO toDTO(Message message) {
    MessageResponseDTO messageResponseDTO = new MessageResponseDTO(message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getAuthorId(),
        message.getChannelId(),
        message.getAttachmentIds());

    return messageResponseDTO;
  }

  @Override
  public String toString() {
    return "Message {\n" +
        "  id=" + getId() + ",\n" +
        "  createdAt=" + getCreatedAt() + ",\n" +
        "  updatedAt=" + getUpdatedAt() + ",\n" +
        "  content='" + content + "',\n" +
        "  authorId=" + authorId + ",\n" +
        "  channelId=" + channelId + "\n" +
        '}';
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
