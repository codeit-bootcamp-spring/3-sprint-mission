package com.sprint.mission.discodeit.dto.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserDTO {
    private UUID userId;
    private String userName;
    private String email;
    private String phoneNumber;
    private String StatusMessage;
    private boolean isOnline;
}
