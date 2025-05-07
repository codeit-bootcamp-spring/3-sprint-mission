package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.dto.CreateUserRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Getter
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private String username;
    private String email;
    private String password;
    //
    private UserStatus userStatus;
    private ReadStatus readStatus;

    @Setter
    private UUID profileId;

    public User(String username, String email, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.ofEpochSecond(Instant.now().getEpochSecond());
        //
        this.username = username;
        this.email = email;
        this.password = password;
        //
        this.profileId = profileId;
        this.userStatus = null;
        this.readStatus = null;
    }

    public void update(String newUsername, String newEmail, String newPassword /*,UUID newProfileId*/) {
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
//        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
//            this.profileId = newProfileId;
//            anyValueUpdated = true;
//        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.ofEpochSecond(Instant.now().getEpochSecond());
        }
    }
}
