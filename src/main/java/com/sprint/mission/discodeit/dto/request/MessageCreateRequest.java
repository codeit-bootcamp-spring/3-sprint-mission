package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;


@Schema(description = "생성할 메세지 정보")
public record MessageCreateRequest(
    @Schema(description = "전송할 메세지 내용", example = "hi")
    String content,

    @Schema(description = "메세지를 전송할 채널의 ID", type = "string", format = "uuid")
    UUID channelId,

    @Schema(description = "메세지를 전송할 사용자 대상의 ID", type = "string", format = "uuid")
    UUID authorId
) {

}
