package com.sprint.mission.discodeit.dto.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserDTO {
    private UUID userId;
    private Instant createdAt;
    private Instant updateAt;
    private String userName;
    private String email;
    private String phoneNumber;
    private String StatusMessage;
    private UUID profileId;
    private boolean isOnline;
}
