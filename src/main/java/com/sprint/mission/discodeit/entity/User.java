package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(
        name = "User",
        description = "사용자 정보를 담고 있는 엔티티"
)
@Entity
@Table(name = "tbl_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseUpdatableEntity {

    @Schema(
            description = "사용자 이름",
            type = "string",
            example = "john_doe",
            minLength = 3,
            maxLength = 50
    )
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Schema(
            description = "사용자 이메일 주소",
            type = "string",
            format = "email",
            example = "john.doe@example.com"
    )
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Schema(
            description = "사용자 비밀번호 (해시된 값)",
            type = "string",
            format = "password",
            example = "********"
    )
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus userStatus;

    public User(String username, String email, String password, BinaryContent profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
        }

        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }

        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
        }

        if (newProfile != null && !newProfile.equals(this.profile)) {
            this.profile = newProfile;
        }
    }

    // 양방향 편의 메소드
    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
        userStatus.setUser(this);
    }
}
