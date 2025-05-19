package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Schema(description = "채널 정보 도메인 모델")
public class Channel implements Serializable {

  private static final long serialVersionUID = 1L;

  // 필드 정의
  @Schema(
      description = "채널 고유 식별 번호",
      example = "150e8430-e94b-41d4-a546-016635440004",
      type = "string",
      format = "uuid"
  )
  private final UUID channelId;

  @Schema(
      description = "채널 생성 시각",
      example = "2025-05-01T00:00:00Z",
      type = "string",
      format = "date-time"
  )
  private final Instant createdAt;

  @Schema(
      description = "채널 수정 시각",
      example = "2025-05-04T01:15:00Z",
      type = "string",
      format = "date-time"
  )
  private Instant updatedAt;

  @Schema(
      description = "채널 이름",
      example = "개발 커뮤니티"
  )
  @NotBlank(message = "채널 이름은 필수입니다")
  private String channelName;

  @Schema(
      description = "채널 유형으로 공개 채널인지 비공개 채널인지 구분합니다",
      example = "PUBLIC",
      type = "string",
      format = "enum"
  )
  @NotBlank(message = "채널 유형( 공개 / 비공개 )은 필수입니다")
  private ChannelType channelType;

  @Schema(
      description = "채널 설명",
      example = "개발자들이 모여 소통하는 공간입니다"
  )
  private String description;


  // 생성자
  public Channel(ChannelType channelType, String channelName, String description) {
    this.channelId = UUID.randomUUID();
    this.createdAt = Instant.now();

    this.channelType = channelType;
    this.channelName = channelName;
    this.description = description;
  }


  // Update
  public void update(String newChannelName, String newDescription) {
    boolean updated = false;
    if (newChannelName != null && !newChannelName.equals(this.channelName)) {
      this.channelName = newChannelName;
      updated = true;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
    }
    if (updated) {
      this.updatedAt = Instant.now();
    } else {
      throw new IllegalArgumentException("No field to update");
    }
  }
}
