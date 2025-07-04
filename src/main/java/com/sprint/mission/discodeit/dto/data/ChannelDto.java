package com.sprint.mission.discodeit.dto.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
        name = "ChannelDto",
        description = "채널 정보를 전달하기 위한 Data Transfer Object"
)
public record ChannelDto(

        UUID id,
        ChannelType type,
        String name,
        String description,
        List<UserDto> participants,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant lastMessageAt

) {
}

