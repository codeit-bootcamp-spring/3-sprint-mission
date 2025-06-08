package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private static final String INVALID_CREDENTIALS = "Invalid username or password";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserResponse login(LoginRequest request) {
        return userRepository.findByUsername(request.username())
            .filter(user -> user.isPasswordMatch(request.password()))
            .map(userMapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException(INVALID_CREDENTIALS));
    }
}
