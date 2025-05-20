package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
        name = "Message",
        description = "채널 내 메시지 정보를 담고 있는 엔티티"
)
@Getter
@ToString
public class Message implements Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(
          description = "메시지의 고유 식별자",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID id;

  @Schema(
          description = "메시지 생성 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant createdAt;

  @Schema(
          description = "메시지 최종 수정 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant updatedAt;

  @Schema(
          description = "메시지 내용",
          type = "string",
          example = "안녕하세요! 반갑습니다."
  )
  private String content;

  @Schema(
          description = "메시지가 속한 채널의 ID",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID channelId;

  @Schema(
          description = "메시지 작성자의 ID",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID authorId;

  @ArraySchema(
          schema = @Schema(type = "string", format = "uuid"),
          arraySchema = @Schema(
                  description = "메시지에 첨부된 파일들의 ID 목록",
                  example = "[\"123e4567-e89b-12d3-a456-426614174000\", \"987fcdeb-51a2-43d8-9876-543210abcdef\"]"
          )
  )
  private List<UUID> attachmentIds;

  public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.content = content;
    this.channelId = channelId;
    this.authorId = authorId;
    this.attachmentIds = attachmentIds;
  }

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
