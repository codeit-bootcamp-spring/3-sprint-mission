package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UpdateChannelRequest(UUID id, String channelName, String description) {
}
