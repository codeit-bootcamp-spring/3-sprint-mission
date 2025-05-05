package com.sprint.mission.discodeit.Dto.authService;

import lombok.Getter;

import java.util.Objects;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user
 * fileName       : LoginRequest
 * author         : doungukkim
 * date           : 2025. 4. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 25.        doungukkim       최초 생성
 */

public record LoginRequest
    (String username,
    String password)

{
    public LoginRequest {
        Objects.requireNonNull(username, "no username in request");
        Objects.requireNonNull(password, "no password in request");
    }
}
