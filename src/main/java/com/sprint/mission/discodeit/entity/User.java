package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "user")
@Table(name = "tbl_users")
@Getter
@NoArgsConstructor
public class User extends BaseUpdatableEntity  {

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) // 1:0..1
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @OneToOne(mappedBy = "user_status",
        cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;


    public User(String username, String email, String password, BinaryContent profile, UserStatus status) {
        super.setId(UUID.randomUUID());
        super.setCreatedAt(Instant.now());
        //
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.status = status;
    }

    public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }
        if (newProfile != null && !newProfile.equals(this.profile)) {
            this.profile = newProfile;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.now());
        }
    }
}
