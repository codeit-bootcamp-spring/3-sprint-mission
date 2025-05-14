package com.sprint.mission.discodeit.entity.dto.user;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
public class UserUpdateRequestDto {

    private UUID userId;
    private String username;
    private String oldPassword;
    private String newPassword;
    private byte[] userProfile;

    public UserUpdateRequestDto(UUID userId, String username, String oldPassword, String newPassword, byte[] userProfile) {
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
        this.userId = userId;
        this.username = username;
        this.userProfile = userProfile;
    }
}
