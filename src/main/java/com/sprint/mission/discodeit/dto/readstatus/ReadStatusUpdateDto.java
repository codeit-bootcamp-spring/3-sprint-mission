package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ReadStatusUpdateDto(
        @NotNull(message = "마지막 읽음 시간은 필수입니다.")
        Instant newLastReadAt) {

}
