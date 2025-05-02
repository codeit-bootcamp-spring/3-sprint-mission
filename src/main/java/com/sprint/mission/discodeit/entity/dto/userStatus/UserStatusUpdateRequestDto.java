package com.sprint.mission.discodeit.entity.dto.userStatus;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@ToString
@Getter
public class UserStatusUpdateRequestDto {

    private UUID userStatusId;
    private Instant nowTime;
}
