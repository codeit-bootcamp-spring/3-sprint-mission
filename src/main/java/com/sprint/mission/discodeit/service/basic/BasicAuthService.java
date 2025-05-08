package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.LoginFailedException;
import com.sprint.mission.discodeit.exception.NotFoundUserStatusException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service("basicAuthService")
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponseDTO login(LoginDTO loginDTO) {
        String name = loginDTO.getName();
        String password = loginDTO.getPassword();;

        // 로그인 성공 시 해당 유저의 마지막 접속 시간 변경
        userRepository.findByNameAndPassword(name, password).ifPresent(user -> {
            user.updateisLogin(true);
            UserStatus userStatus = findUserStatus(user.getId());
            userStatus.updateLastLoginTime(Instant.now());
            userRepository.save(user);
            userStatusRepository.save(userStatus);
        });

        return userRepository.findByNameAndPassword(name, password)
                .map(UserResponseDTO::toDTO)
                .orElseThrow(LoginFailedException::new);
    }

    private UserStatus findUserStatus(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(NotFoundUserStatusException::new);
    }
}
