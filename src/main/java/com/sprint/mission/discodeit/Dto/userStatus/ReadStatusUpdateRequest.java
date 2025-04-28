package com.sprint.mission.discodeit.Dto.userStatus;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.userStatus
 * fileName       : ReadStatusUpdateRequest
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */
@Getter
public class ReadStatusUpdateRequest {
    UUID readStatusId;
    Instant newTime;

    public ReadStatusUpdateRequest(UUID readStatusId, Instant newTime) {
        this.readStatusId = readStatusId;
        this.newTime = newTime;
    }
}
