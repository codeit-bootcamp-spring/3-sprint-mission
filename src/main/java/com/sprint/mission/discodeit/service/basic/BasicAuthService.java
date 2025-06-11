package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserDto login(LoginRequest request) {
        //1. Username으로 user 찾기
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("user", ErrorMessages.ERROR_NOT_FOUND)));

        //2. password 일치 여부 확인
        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException(
                ErrorMessages.format("password", ErrorMessages.ERROR_MISMATCH));
        }
        return userMapper.toDto(user);
    }
}
