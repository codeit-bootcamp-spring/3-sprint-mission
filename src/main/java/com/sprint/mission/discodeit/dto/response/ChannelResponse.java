package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "채널 응답 DTO")
public record ChannelResponse(
    UUID id,
    ChannelType type,
    String name,
    String description,
    List<UserResponse> participants,
    Instant lastMessageAt
) {

}
