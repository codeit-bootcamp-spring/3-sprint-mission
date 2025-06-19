package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.exception.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserResponse login(LoginRequest request) {
        String username = request.username();
        String password = request.password();

        log.info("[BasicAuthService] Login attempt received. [username={}]", username);

        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.warn("[BasicAuthService] User not found. [username={}]", username);
                return new UserNotFoundException(username);
            });

        if (!user.getPassword().equals(password)) {
            log.warn("[BasicAuthService] Invalid password. [username={}]", username);
            throw new InvalidPasswordException();
        }

        log.debug("[BasicAuthService] Login successful. [userId={}]", user.getId());
        return userMapper.toResponse(user);
    }
}
