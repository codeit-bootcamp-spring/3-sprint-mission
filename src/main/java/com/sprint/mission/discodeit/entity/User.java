package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;
import java.time.Instant;
import lombok.Getter;

@Getter
public class User implements Serializable {

    private final UUID userId;
    private String userName;
    private String email;
    private String password;
    private final Instant createdAt;
    private Instant updatedAt;
    private static final long serialVersionUID = 1L;

    // 생성자
    public User(String userName, String email, String password) {
        this.userId = UUID.randomUUID();
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.createdAt = Instant.now(); // 현재 시간으로 생성 시간 초기화
        this.updatedAt = this.createdAt;
    }

    // 이메일 업데이트
    public void updateEmail(String email) {
        if (email != null && !email.isEmpty()) {
            this.email = email;
            this.updatedAt = Instant.now();
        }
    }

    // 사용자명 업데이트
    public void updateUserName(String userName) {
        if (userName != null && !userName.isEmpty()) {
            this.userName = userName;
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

    @Override
    public String toString() {
        return "User [userId=" + userId + ", userName=" + userName + ", email=" + email + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + ", password=" + password + "]";
    }
}
