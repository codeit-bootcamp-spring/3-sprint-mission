package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record BinaryContentDTO(

    UUID id,
    String fileName,
    Long size,
    String contentType

) {

}
