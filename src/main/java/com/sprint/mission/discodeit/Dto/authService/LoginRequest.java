package com.sprint.mission.discodeit.Dto.authService;

import jakarta.validation.constraints.NotNull;

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
    (
            @NotNull(message = "아이디가 반드시 필요합니다.") String username,
            @NotNull(message = "비밀번호가 반드시 필요합니다.") String password
    )
{ }
