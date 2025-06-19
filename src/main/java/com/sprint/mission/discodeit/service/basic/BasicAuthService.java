package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.global.exception.InvalidPasswordException;
import com.sprint.mission.discodeit.global.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserResponse login(LoginRequest request) {
        String username = request.username();
        String password = request.password();

        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

        if (!user.getPassword().equals(password)) {
            throw new InvalidPasswordException();
        }

        return userMapper.toResponse(user);
    }
}
