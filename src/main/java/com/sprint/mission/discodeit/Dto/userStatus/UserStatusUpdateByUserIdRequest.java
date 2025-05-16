package com.sprint.mission.discodeit.Dto.userStatus;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.userStatus fileName       :
 * UserStatusUpdateByUserIdRequest author         : doungukkim date           : 2025. 5. 9.
 * description    : =========================================================== DATE AUTHOR
 *    NOTE ----------------------------------------------------------- 2025. 5. 9. doungukkim
 * 최초 생성
 */
public record UserStatusUpdateByUserIdRequest(Instant newLastActiveAt) {

}
