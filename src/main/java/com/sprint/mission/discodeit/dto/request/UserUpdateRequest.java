package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class UserUpdateRequest {
    private String userName;
    // pwd 필드는 별도의 dto로 표현
    private String email;
    private String phoneNumber;
    private String StatusMessage;
}
