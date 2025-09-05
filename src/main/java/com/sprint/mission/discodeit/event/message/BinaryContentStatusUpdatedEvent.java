package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.time.Instant;

public record BinaryContentStatusUpdatedEvent(

    BinaryContentDto data,
    Instant createdAt
){


}
