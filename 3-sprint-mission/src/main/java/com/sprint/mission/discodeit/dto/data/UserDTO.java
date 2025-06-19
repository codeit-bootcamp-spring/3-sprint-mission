package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record UserDTO(

    UUID id,
    String username,
    String email,
    BinaryContentDTO profile,
    Boolean online

) {

}
