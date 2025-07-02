package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", columnDefinition = "uuid")
    private BinaryContent profile;

    @JsonManagedReference
    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;

    @Builder
    public User(String username, String email, String password, BinaryContent profile,
            UserStatus status) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.status = status;
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
}
