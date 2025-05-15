package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private String password;
    private UUID profileId;

    public User(String username, String email, String password,UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.username = username;
        this.email = email;
        this.password = password;
    }


    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
        if (newUsername == null) {
            throw new IllegalArgumentException("[error] 사용자 이름이 입력되지 않았습니다.");
        }
        if (newUsername.equals(this.username)) {
            throw new IllegalArgumentException("[error] 현재 사용자 이름과 동일합니다.");
        }
        this.username = newUsername;

        if (newEmail == null) {
            throw new IllegalArgumentException("[error] 이메일이 입력되지 않았습니다.");
        }
        if (newEmail.equals(this.email)) {
            throw new IllegalArgumentException("[error] 현재 이메일과 동일합니다.");
        }
        this.email = newEmail;

        if (newPassword == null) {
            throw new IllegalArgumentException("[error] 비밀번호가 입력되지 않았습니다.");
        }
        if (newPassword.equals(this.password)) {
            throw new IllegalArgumentException("[error] 현재 비밀번호와 동일합니다.");
        }
        this.password = newPassword;

        if (profileId != null && newProfileId == null) {
            System.out.println("사용자 프로필 사진이 삭제되었습니다.");
        }
        if (newProfileId.equals(this.profileId)) {
            throw new IllegalArgumentException("[error] 현재 프로필 ID와 동일합니다.");
        } else {
            this.profileId = newProfileId;
        }

        this.updatedAt = Instant.now();
    }


}

