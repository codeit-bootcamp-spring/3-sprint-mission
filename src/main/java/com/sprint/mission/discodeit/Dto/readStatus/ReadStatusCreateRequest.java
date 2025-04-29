package com.sprint.mission.discodeit.Dto.readStatus;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.userStatus
 * fileName       : ReadStatusCreateRequest
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public record ReadStatusCreateRequest(UUID userId, UUID channelId) {
}

