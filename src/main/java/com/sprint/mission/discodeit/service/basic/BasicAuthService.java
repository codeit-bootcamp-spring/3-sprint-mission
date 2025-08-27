package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.userException.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.jpa.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic fileName       : BasicAuthService
 * author         : doungukkim date           : 2025. 4. 25. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 25.        doungukkim 최초 생성
 */

@Slf4j
@Primary
@RequiredArgsConstructor
@Service("basicAuthService")
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getCurrentUserInfo(UserDetails userDetails) {
        if(userDetails == null) {
            log.warn("[AuthController] 유저 인증 실패");
            throw new UserNotFoundException();
        }

        String username = userDetails.getUsername();
        log.info("[AuthService] 조회할 사용자 이름: " + username);
        User currentUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        return userMapper.toDto(currentUser);
    }
}


