package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 보안성 향상의 목적으로 pwd update 별도 관리
public final class UserPasswordUpdateRequest {
    String currentPassword;
    String newPassword;
}
