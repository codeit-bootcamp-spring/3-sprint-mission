package com.sprint.mission.discodeit.Dto.user;

import com.sprint.mission.discodeit.Dto.binaryContent.JpaBinaryContentResponse;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user fileName       : CreateUserResponse author
 * : doungukkim date           : 2025. 5. 15. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 5. 15.        doungukkim 최초 생성
 */

public record CreateUserResponse(

    UUID id,
    String username,
    String email,
    JpaBinaryContentResponse profile,
    boolean online
) {

}
