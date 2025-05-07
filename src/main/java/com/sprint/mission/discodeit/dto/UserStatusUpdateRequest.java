package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.UUID;

//수정 대상 객체의 id 파라미터, 수정할 값 파라미터
public record UserStatusUpdateRequest(UUID userStatusId, UserStatusType status) {
}

