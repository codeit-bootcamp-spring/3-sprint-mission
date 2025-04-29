package com.sprint.mission.discodeit.Dto.authService;

import lombok.Getter;

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

{}
