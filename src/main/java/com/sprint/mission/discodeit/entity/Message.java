package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Message implements Serializable {

  private static final long serialVersionUID = 1L;
  //
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  //
  private String content;
  //
  private final UUID authorId;
  private final UUID channelId;
  private List<UUID> attachmentIds;  // BinaryContent의 id

  public Message(String content, UUID authorId, UUID channelId, List<UUID> attachmentIds) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    //
    this.content = content;
    this.authorId = authorId;
    this.channelId = channelId;
    this.attachmentIds = attachmentIds;
  }

  //QUESTION. updateRequest도 안에 id를 포함할께 아니라 id, 변화될 필드 이렇게 나누는게 나을까?
  // FIXME. attachmentIds optional로 설정한거 전체적으로 수정하기
  public void update(String content, Optional<List<UUID>> attachmentIds) {
    boolean anyValueUpdated = false;
    if (content != null && !content.equals(this.content)) {
      this.content = content;
      anyValueUpdated = true;
    }

//    if (attachmentIds != null && !attachmentIds.equals(this.attachmentIds)) {
//      this.attachmentIds = attachmentIds;
//      anyValueUpdated = true;
//    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }

  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault());

    String createdAtFormatted = formatter.format(createdAt);
    String updatedAtFormatted = formatter.format(updatedAt);

    return "💬 Message {\n" +
        " id         = " + id + "\n" +
        " createdAt  = " + createdAtFormatted + "\n" +
        " updatedAt  = " + updatedAtFormatted + "\n" +
        " content       = '" + content + "'\n" +
        " authorId     = " + authorId + "\n" +
        " channelId     = " + channelId + "\n" +
        " attachmentIds     = " + attachmentIds + "\n" +
        "}";
  }
}
