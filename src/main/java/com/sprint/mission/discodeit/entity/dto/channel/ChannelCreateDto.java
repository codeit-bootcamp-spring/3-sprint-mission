package com.sprint.mission.discodeit.entity.dto.channel;

import com.sprint.mission.discodeit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelCreateDto {
    private User admin;
    private String name;
    private String description;

}
