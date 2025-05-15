package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record CreatePrivateChannelRequest(UUID participantId1,
                                          UUID participantId2) {
}
