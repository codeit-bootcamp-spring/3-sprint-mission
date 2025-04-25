package com.sprint.mission.discodeit.Dto.userStatus;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user
 * fileName       : ProfileUploadResponse
 * author         : doungukkim
 * date           : 2025. 4. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 24.        doungukkim       최초 생성
 */
@Getter
@Setter
public class ProfileUploadResponse {
    Instant createdAt;
    Instant updatedAt;
    UUID id;
    String username;
    String email;
    UUID profileId;

    public ProfileUploadResponse(Instant createdAt, Instant updatedAt, UUID id, String username, String email, UUID profileId) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileId = profileId;
    }

    @Override
    public String toString() {
        return "ProfileUploadResponse{" +
                "createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", profileId=" + profileId +
                '}';
    }
}
