package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "비공개 채널 생성 요청 DTO")
public record PrivateChannelCreateRequest(
    List<UUID> participantIds
) {

}
