package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.function.Supplier;

public record BinaryContentCreatedEvent(
    BinaryContentDto meta,
    String objectKey,
    Supplier<InputStream> inputStreamSupplier
) {

}
