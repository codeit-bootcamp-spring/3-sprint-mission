package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UpdateReadStatusRequest (UUID id, UUID userId,UUID channelId){
}
