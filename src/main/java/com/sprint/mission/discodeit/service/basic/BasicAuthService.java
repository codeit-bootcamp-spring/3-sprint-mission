package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.LoginFailedException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserStatusException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;

import java.time.Instant;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("basicAuthService")
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto login(LoginDto loginDTO) {
        String username = loginDTO.username();
        String password = loginDTO.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundUserException("username이 " + username + "인 사용자를 찾을 수 없습니다."));

        if (!user.getPassword().equals(password)) {
            throw new LoginFailedException();
        }

        // User 로그인 시 User 온라인 상태 정보 변경
        UserStatus userStatus = findUserStatus(user.getId());
        userStatus.updatelastActiveAt(Instant.now());
        userRepository.save(user);
        userStatusRepository.save(userStatus);

        return userMapper.toDto(user);
    }

    private UserStatus findUserStatus(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(NotFoundUserStatusException::new);
    }
}
