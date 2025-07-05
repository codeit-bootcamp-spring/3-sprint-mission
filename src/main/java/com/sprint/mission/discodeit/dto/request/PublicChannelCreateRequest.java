package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record PublicChannelCreateRequest(
    UUID creator,
    String name,
    String description
) {

}
