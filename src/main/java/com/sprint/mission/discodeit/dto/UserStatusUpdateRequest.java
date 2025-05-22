package com.sprint.mission.discodeit.dto;

import java.time.Instant;

// 수정할 값 파라미터
public record UserStatusUpdateRequest(Instant newLastActiveAt) {

//  Deprecated(newStatus 필드) 파라미터로 안넘어올경우, default 값 넣어줌
//  public UserStatusUpdateRequest(Instant newLastActiveAt) {
//    this(newLastActiveAt, UserStatusType.ONLINE);
//  }
}

