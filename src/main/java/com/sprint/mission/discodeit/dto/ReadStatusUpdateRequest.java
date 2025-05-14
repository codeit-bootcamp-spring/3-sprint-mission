package com.sprint.mission.discodeit.dto;

import java.time.Instant;

// 수정할 값 파라미터
public record ReadStatusUpdateRequest(Instant lastReadAt) {
}

