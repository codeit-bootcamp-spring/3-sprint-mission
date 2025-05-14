package com.sprint.mission.discodeit.entity.dto.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class GetPublicChannelRequestDto {
    private UUID channelId;
}
