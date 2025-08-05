package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service("basicAuthService")
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto getCurrentUser(UserDetails userDetails) {
        log.debug("[AuthService] 현재 사용자 정보 요청");

        if (userDetails == null) {
            log.warn("[AuthService] UserDetails가 null입니다.");
            return null;
        }

        String username = userDetails.getUsername();
        log.debug("[AuthService] 조회할 사용자명: {}", username);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundUserException("사용자를 찾을 수 없습니다: " + username));

        log.debug("[AuthService] 조회된 사용자 정보: {}", user);

        return userMapper.toDto(user);
    }
}
