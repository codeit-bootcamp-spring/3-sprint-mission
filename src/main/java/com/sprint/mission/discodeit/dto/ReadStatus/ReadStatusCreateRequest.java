package com.sprint.mission.discodeit.dto.ReadStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(

        @NotNull(message = "사용자 ID는 필수업니다.")
        UUID userId,

        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId,

        @NotNull(message = "마지막으로 읽은 시간은 빈 값일 수 없습니다.")
        @PastOrPresent(message = "마지막으로 읽은 시간은 현재 시각 이전이어야 합니다.")
        Instant lastReadAt
) {

}
