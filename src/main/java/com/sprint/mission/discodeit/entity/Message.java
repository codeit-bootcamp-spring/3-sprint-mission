package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Message implements Serializable {

  private static final long serialVersionUID = 1L;
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  private String content;
  private final UUID authorId;
  private final UUID channelId;
  private List<UUID> attachmentIds;

  public Message(UUID authorId, UUID channelId, String content, List<UUID> attachmentIds) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.content = content;
    this.authorId = authorId;
    this.channelId = channelId;
    this.attachmentIds = attachmentIds;
  }

  public void updateContent(String content) {
    this.content = content;
    this.updatedAt = Instant.now();
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
        "  id=" + id + ",\n" +
        "  createdAt=" + createdAt + ",\n" +
        "  updatedAt=" + updatedAt + ",\n" +
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
    return Objects.equals(id, message.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
