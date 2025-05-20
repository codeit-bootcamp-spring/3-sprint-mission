package com.sprint.mission.discodeit.entity.dto.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelCreateDto {
    private UUID adminId;
    private String name;
    private String description;

}
