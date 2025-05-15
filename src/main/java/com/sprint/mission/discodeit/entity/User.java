package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;
import java.time.Instant;
import lombok.Getter;

@Getter
public class User implements Serializable {

    private final UUID userId;
    private String username;
    private String email;
    private String password;
    private final Instant createdAt;
    private Instant updatedAt;
    private static final long serialVersionUID = 1L;
    private UUID profileId;

    // 생성자
    public User(String username, String email, String password, UUID profileId) {
        this.userId = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = Instant.now(); // 현재 시간으로 생성 시간 초기화
        this.updatedAt = this.createdAt;
        this.profileId = profileId;
    }

    // 이메일 업데이트
    public void updateEmail(String email) {
        if (email != null && !email.isEmpty()) {
            this.email = email;
            this.updatedAt = Instant.now();
        }
    }

    // 사용자명 업데이트
    public void updateUsername(String username) {
        if (username != null && !username.isEmpty()) {
            this.username = username;
        }
        this.updatedAt = Instant.now();
    }

    //비밀번호 업데이트
    public void updatePassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
        this.updatedAt = Instant.now();
    }

    //프로필 아이디 업데이트
    public void updateProfileId(UUID profileId) {
        if (profileId != null) {
            this.profileId = profileId;
        }
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", username=" + username + ", email=" + email + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + ", password=" + password + "]";
    }
}
