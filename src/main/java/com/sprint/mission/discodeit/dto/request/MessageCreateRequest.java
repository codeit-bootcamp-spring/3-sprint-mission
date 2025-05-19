package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;


@Getter
@AllArgsConstructor
@Schema(description = "생성할 메세지 정보")
public final class MessageCreateRequest {

  @Schema(description = "전송할 메세지 내용", example = "hi")
  @NotBlank(message = "전송할 메세지의 내용을 작성해주세요")
  private final String messageContent;

  @Schema(description = "메세지를 전송할 채널의 ID", type = "string", format = "uuid")
  @NotBlank(message = "전송할 채널의 ID는 필수입니다")
  private final UUID channelId;

  @Schema(description = "메세지를 전송할 사용자 대상의 ID", type = "string", format = "uuid")
  @NotBlank(message = "메세지를 전송할 사용자 대상의 ID는 필수입니다")
  private final UUID authorId;
}
