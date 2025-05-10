package com.sprint.mission.discodeit.Dto.userStatus;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.userStatus
 * fileName       : UserStatusUpdateByUserIdRequest
 * author         : doungukkim
 * date           : 2025. 5. 9.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 9.        doungukkim       최초 생성
 */
@Getter
public class UserStatusUpdateByUserIdRequest {
    private final UUID userId;
    private final Instant newTime;

    public UserStatusUpdateByUserIdRequest(String userId, String newTime) {


        this.userId = UUID.fromString(userId);
        if((newTime == null) || ( newTime.isBlank())){
            this.newTime = Instant.now();
        } else{
            try{
                this.newTime = Instant.parse(newTime);
            } catch (Exception e) {
                throw new RuntimeException("wrong Instant input");
            }
        }
    }
}
