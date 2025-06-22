package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @NotBlank(message = "사용자 ID는 필수입니다")
    @Positive(message = "사용자 ID는 양수여야합니다")
    UUID userId,
    @NotBlank(message = "채널 ID는 필수입니다")
    @Positive(message = "채널 ID는 양수여야합니다")
    UUID channelId,
    @PastOrPresent(message = "미래 시간은 허용되지 않습니다")
    Instant lastReadAt) {

}

