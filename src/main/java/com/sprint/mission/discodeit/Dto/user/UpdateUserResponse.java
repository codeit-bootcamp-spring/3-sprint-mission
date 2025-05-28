package com.sprint.mission.discodeit.Dto.user;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user fileName       : UpdateUserResponse author
 * : doungukkim date           : 2025. 5. 15. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 5. 15.        doungukkim 최초 생성
 */
public record UpdateUserResponse(Instant createdAt,
                                 Instant updatedAt,
                                 String username,
                                 String email,
                                 String password,
                                 BinaryContent profileId) {

}
