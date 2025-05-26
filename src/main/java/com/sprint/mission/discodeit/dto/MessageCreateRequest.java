package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Message 생성 정보")
public record MessageCreateRequest(UUID channelId, UUID authorId, String content) {

}
