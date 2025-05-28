package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Schema(
        name = "Channel",
        description = "채널 정보를 담고 있는 엔티티"
)
@Getter
public class Channel implements Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(
          description = "채널의 고유 식별자",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID id;

  @Schema(
          description = "채널 생성 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant createdAt;

  @Schema(
          description = "채널 최종 수정 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant updatedAt;

  @Schema(
          description = "채널 타입(PUBLIC/PRIVATE)",
          type = "string",
          example = "PUBLIC"
  )
  private ChannelType type;

  @Schema(
          description = "채널 이름",
          type = "string",
          example = "일반",
          minLength = 1,
          maxLength = 100
  )
  private String name;

  @Schema(
          description = "채널 설명",
          type = "string",
          example = "일반적인 대화를 나누는 채널입니다"
  )
  private String description;

  public Channel(ChannelType type, String name, String description) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void update(String newName, String newDescription) {
    boolean anyValueUpdated = false;
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
      anyValueUpdated = true;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}
