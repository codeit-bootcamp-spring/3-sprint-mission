package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import java.util.UUID;

public record BinaryContentResponseDto(UUID id, String fileName, Long size,
                                       String contentType, BinaryContentStatus status) {

}
