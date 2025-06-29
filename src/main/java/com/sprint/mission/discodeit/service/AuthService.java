package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto login(LoginRequest request) {
        return userRepository.findByUsernameAndPassword(request.username(), request.password())
            .map(userMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 username & password 입니다."));
    }
}
