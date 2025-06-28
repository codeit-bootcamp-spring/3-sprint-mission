package com.sprint.mission.discodeit.dto.UserStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;

public record UserStatusUpdateRequest(

        @NotNull(message = "마지막 활동 시간은 빈 값일 수 없습니다.")
        @PastOrPresent(message = "마지막 활동 시간은 현재 시각 이전이어야 합니다.")
        Instant newLastActiveAt
) {

}