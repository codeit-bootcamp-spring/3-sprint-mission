package com.sprint.mission.discodeit.Dto.user;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user
 * fileName       : UserFindDto
 * author         : doungukkim
 * date           : 2025. 4. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 24.        doungukkim       최초 생성
 */

public record UserFindResponse
     (UUID id,
     Instant createdAt,
     Instant updatedAt,
     String username,
     String email,
     BinaryContent profile,
     boolean online){

}
