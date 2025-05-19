package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class User implements Serializable {

    private static final long serialVersionUID = -3212462601778766776L;

    /* 공통 필드 */
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private String username;        // 유저 이름
    private String email;           // 이메일
    private String password;        // 비밀번호
    private UUID profileId;         // 프로필 사진

    @Builder
    public User(String username, String email, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
        boolean isUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            isUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            isUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            isUpdated = true;
        }
        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
            isUpdated = true;
        }

        if (isUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
