package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;

// 수정할 값 파라미터
public record ReadStatusUpdateRequest(Instant newLastReadAt) {

}

