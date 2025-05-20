package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record UpdateChannelRequest(String newName,
                                   String newDescription) {
}
