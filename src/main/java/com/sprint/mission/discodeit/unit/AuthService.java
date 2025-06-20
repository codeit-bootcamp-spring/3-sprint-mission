package com.sprint.mission.discodeit.unit;

import com.sprint.mission.discodeit.dto.authService.LoginRequest;
import com.sprint.mission.discodeit.dto.authService.LoginResponse;

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
    LoginResponse login(LoginRequest request);

}
