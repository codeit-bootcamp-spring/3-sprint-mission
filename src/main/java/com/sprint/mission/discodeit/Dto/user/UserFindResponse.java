package com.sprint.mission.discodeit.Dto.user;

import com.sprint.mission.discodeit.Dto.binaryContent.JpaBinaryContentResponse;

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
     String username,
     String email,
      JpaBinaryContentResponse profile,
     boolean online){

}
