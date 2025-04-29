package com.sprint.mission.discodeit.Dto.userStatus;

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
public record ProfileUploadRequest
     (UUID userId,
     byte[] image){
}

