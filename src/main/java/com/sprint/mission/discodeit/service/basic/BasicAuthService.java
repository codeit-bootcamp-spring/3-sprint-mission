package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidUserPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundByNameException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Objects;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private static final Logger logger = Logger.getLogger(BasicAuthService.class.getName()); // 필드로 Logger 선언

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto login(LoginRequest request) {

        String username = request.username();
        String password = request.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundByNameException(username));

        if (!Objects.equals(user.getPassword(), password)) {
            throw new InvalidUserPasswordException(password);
        }

        return userMapper.toDto(user);
    }
}

