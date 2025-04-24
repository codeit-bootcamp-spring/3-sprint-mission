package com.sprint.mission.discodeit.Dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user
 * fileName       : ProfileUploadRequest
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
public class ProfileUploadRequest {
    private UUID userId;
    private byte[] image;

    public ProfileUploadRequest(UUID userId, byte[] image) {
        this.userId = userId;
        this.image = image;
    }
}
