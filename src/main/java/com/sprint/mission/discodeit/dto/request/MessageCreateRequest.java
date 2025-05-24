package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;


@Getter
@AllArgsConstructor
@Schema(description = "생성할 메세지 정보")
public final class MessageCreateRequest {

  @Schema(description = "전송할 메세지 내용", example = "hi")
  private final String content;

  @Schema(description = "메세지를 전송할 채널의 ID", type = "string", format = "uuid")
  private final UUID channelId;

  @Schema(description = "메세지를 전송할 사용자 대상의 ID", type = "string", format = "uuid")
  private final UUID authorId;
}
