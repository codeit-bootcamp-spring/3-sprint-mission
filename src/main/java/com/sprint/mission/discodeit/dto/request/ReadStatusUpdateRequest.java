package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    @NotNull(message = "새롭게 마지막으로 읽은 시간은 필수입니다")
    @PastOrPresent(message = "마지막으로 읽은 시간은 현재 또는 과거의 시간이어야 합니다")
    Instant newLastReadAt
) {

}
