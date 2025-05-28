package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Message 생성 및 수정 정보")
public record MessageRequestDto(String content, UUID channelId, UUID authorId) {

}
