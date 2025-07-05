package com.sprint.mission.discodeit.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * packageName    : com.sprint.mission.discodeit.Dto
 * fileName       : UserServiceDto
 * author         : doungukkim
 * date           : 2025. 4. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 24.        doungukkim       최초 생성
 */

@Builder
public record UserCreateRequest(
        @NotBlank String username,
        @Email String email,
        @NotBlank String password
) { }

