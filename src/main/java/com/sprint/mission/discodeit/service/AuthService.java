package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.authService.LoginRequest;
import com.sprint.mission.discodeit.Dto.authService.LoginResponse;
import com.sprint.mission.discodeit.entity.User;

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
