package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// pwd update 별도 관리
public class UserPasswordUpdateRequest {
    String currentPassword;
    String newPassword;
}
