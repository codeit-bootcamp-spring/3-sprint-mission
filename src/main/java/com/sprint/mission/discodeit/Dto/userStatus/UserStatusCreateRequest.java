package com.sprint.mission.discodeit.Dto.userStatus;

import lombok.Getter;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.userStatus
 * fileName       : UserStatusCreatRequest
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */
@Getter
public class UserStatusCreateRequest {
    private UUID userId;

    public UserStatusCreateRequest(UUID userId) {
        this.userId = userId;
    }
}
