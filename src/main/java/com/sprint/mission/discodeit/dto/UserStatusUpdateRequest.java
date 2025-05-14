package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatusType;

// 수정할 값 파라미터
public record UserStatusUpdateRequest(UserStatusType status) {
}

