package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record BinaryContentCreateEvent(UUID id, byte[] bytes) {

}
