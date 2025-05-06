package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class User implements Serializable {
    private final UUID id;
    private final String username;
    private String email;
    private String password;
    private String name;
    private UUID profileId;
    private boolean isLogin;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant loginTime;

    public User(String username, String email, String password, String name) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileId = null;
        this.isLogin = false;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.loginTime = Instant.now();
    }

    // Date 타입 포매팅
    public String getCreatedAt() {
        String formattedCreatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        return formattedCreatedTime;
    }

    public String getUpdatedAt() {
        String formattedUpdatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updatedAt);
        return formattedUpdatedTime;
    }

    public String getLoginTime() {
        String formattedLoginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(loginTime);
        return formattedLoginTime;
    }

    @Override
    public String toString() {

        return "User{" +
//                "id=" + id +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                ", loginTime=" + getLoginTime() +
                ", isLogin=" + isLogin() +
                '}';
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void updateEmail(String email) {
        this.email = email;
        this.updatedAt = Instant.now();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updatedAt = Instant.now();
    }

    public void updateProfile(UUID newProfileId) {
        this.profileId = newProfileId;
        this.updatedAt = Instant.now();
    }

    public void updateLoginTime() {
        this.loginTime = Instant.now();
    }
}

