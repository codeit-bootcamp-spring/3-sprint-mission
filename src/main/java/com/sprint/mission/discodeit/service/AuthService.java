package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import org.springframework.security.core.userdetails.UserDetails;

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

    UserResponse getCurrentUserInfo(UserDetails userDetails);

}
