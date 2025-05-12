package com.sprint.mission.discodeit.Dto.user;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user
 * fileName       : UserUpdateNameRequest
 * author         : doungukkim
 * date           : 2025. 5. 9.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 9.        doungukkim       최초 생성
 */
public record UserUpdateNameRequest(UUID userId, String name) {
}
