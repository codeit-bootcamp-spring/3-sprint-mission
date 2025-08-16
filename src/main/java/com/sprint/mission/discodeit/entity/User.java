package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private Role role;

    public User(String username, String email, String password, BinaryContent profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.status = new UserStatus(this, Instant.now());
        this.role = Role.USER;
    }

    public void update(String newUsername, String newEmail, String newPassword,
                       BinaryContent newProfile) {
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

    public void updateRole(Role newRole) {
        if (this.role != newRole) {
            this.role = newRole;
        }
    }
}