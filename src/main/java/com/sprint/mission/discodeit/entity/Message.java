package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Schema(description = "메세지 정보 도메인 모델")
public class Message implements Serializable {

  private static final long serialVersionUID = 1L;

  // 필드 정의
  @Schema(
      description = "메세지 고유 식별 번호",
      example = "615e8040-e29b-41d4-a716-953816706615",
      type = "string",
      format = "uuid"
  )
  private final UUID id;

  @Schema(
      description = "메세지 생성 시각",
      example = "2025-03-12T16:23:46Z",
      type = "string",
      format = "date-time"
  )
  private final Instant createdAt;

  @Schema(
      description = "메세지 수정 시각",
      example = "2025-03-17T15:49:40Z",
      type = "string",
      format = "date-time"
  )
  private Instant updatedAt;

  @Schema(
      description = "메세지 내용을 기재합니다",
      example = "자니...?"
  )
  @NotNull(message = "메세지 내용은 필수로 입력해주셔야합니다")
  private String content;

  @Schema(
      description = "메세지가 전송될 채널의 고유 식별 번호",
      example = "c1a3c2d3-5ef1-4d0a-96de-2344dc987ccc",
      type = "string",
      format = "uuid"
  )
  private UUID channelId;

  @Schema(
      description = "메세지를 전송할 사용자의 고유 식별 번호",
      example = "a7d1c2b4-fa1c-4ed1-9c4a-11ua3cef3456",
      type = "string",
      format = "uuid"
  )
  private UUID authorId;

  // BinaryContent 모델 참조 ID
  @Schema(
      description = "메세지 전송 시 포함될 수도 있는 첨부파일의 고유 식별 번호",
      example = "0bd1a8d0-643e-43e5-9fcb-65123987ec2a0",
      type = "string",
      format = "uuid",
      nullable = true
  )
  private List<UUID> attachmentIds;

  // 생성자
  public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.content = content;
    this.channelId = channelId;
    this.authorId = authorId;
    //
    this.attachmentIds = attachmentIds;
  }

  // Update
  public void update(String newContent) {
    boolean updated = false;
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
      updated = true;
    }
    if (updated) {
      this.updatedAt = Instant.now();
    } else {
      throw new IllegalArgumentException("No field to update");
    }
  }
}
