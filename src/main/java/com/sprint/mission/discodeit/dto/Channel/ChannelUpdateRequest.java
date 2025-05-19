package com.sprint.mission.discodeit.dto.Channel;

import lombok.Builder;

@Builder
public record ChannelUpdateRequest(
        String newName,
        String newDescription

) {

}
