package com.sprint.mission.discodeit.dto.data;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDTO(

    UUID id,
    String username,
    String email,
    BinaryContentDTO profile,
    Boolean online

) {

}
