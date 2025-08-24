package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : AuthService
 * author         : doungukkim
 * date           : 2025. 4. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 25.        doungukkim       최초 생성
 */
public interface AuthService {

    UserDto updateRole(UserRoleUpdateRequest request);

    UserDto updateRoleInternal(UserRoleUpdateRequest request);

    JwtInformation refreshToken(String refreshToken);

}
