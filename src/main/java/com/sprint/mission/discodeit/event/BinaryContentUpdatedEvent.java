package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import java.util.UUID;

public record BinaryContentUpdatedEvent(
    UUID fileId,
    BinaryContentDto dto
) {}
