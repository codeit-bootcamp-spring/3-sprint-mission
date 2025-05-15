package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record UpdateMessageRequest(UUID id, String text) {
}
