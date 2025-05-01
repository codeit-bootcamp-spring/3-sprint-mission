package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record PublicChannelUpdateRequest(UUID channelId, String name, String description) {

}
