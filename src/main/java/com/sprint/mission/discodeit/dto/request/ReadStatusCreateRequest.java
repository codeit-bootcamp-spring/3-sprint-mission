package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(

        @NotNull(message = "유저 ID는 필수입니다.")
        UUID userId,

        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId,

        @PastOrPresent(message = "마지막 조회 시간은 현재 시간 또는 과거 시간이어야 합니다.")
        Instant lastReadAt
) { }
