package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Message 생성 요청 객체")
public record MessageCreateRequest(String content, UUID userId, UUID channelId) {

}

