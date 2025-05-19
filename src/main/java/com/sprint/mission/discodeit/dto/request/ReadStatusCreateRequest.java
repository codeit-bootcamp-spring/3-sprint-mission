package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "생성할 메세지 읽음 상태 정보")
public final class ReadStatusCreateRequest {

  @Schema(description = "확인할 채널 ID", type = "string", format = "uuid")
  @NotBlank(message = "메세지 읽음 상태 정보를 생성할 채널의 ID는 필수입니다")
  private UUID channelId;

  @Schema(description = "읽음 상태 정보가 적용될 사용자 ID", type = "string", format = "uuid")
  @NotBlank(message = "읽음 상태 정보가 생성될 사용자의 ID는 필수입니다")
  private UUID userId;

  @Schema(description = "마지막으로 메세지를 읽은 시간", type = "string", format = "date-time")
  @NotBlank
  private Instant lastReadAt;
}
