package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidUsernameOrPasswordException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserDto login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        log.info("로그인 인증 시작 - username: {}", username);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.warn("로그인 실패 - 존재하지 않는 사용자명: {}", loginRequest.username());
                return new InvalidUsernameOrPasswordException(username);
            });

        if (!user.getPassword().equals(password)) {
            log.warn("로그인 실패 - 비밀번호 불일치, username: {}", username);
            throw new InvalidUsernameOrPasswordException(username);
        }

        log.info("로그인 성공 - userId: {}, username: {}", user.getId(), username);

        return userMapper.toDto(user);
    }
}
