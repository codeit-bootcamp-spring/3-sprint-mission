package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.authService.LoginRequest;
import com.sprint.mission.discodeit.Dto.authService.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicAuthService
 * author         : doungukkim
 * date           : 2025. 4. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 25.        doungukkim       최초 생성
 */

@Primary
@RequiredArgsConstructor
@Service("basicAuthService")
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;


    public ResponseEntity<?> login(LoginRequest request) {
        String username = request.username();
        String password = request.password();

        List<User> users = userRepository.findAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                UserStatus userStatus = userStatusRepository.findUserStatusByUserId(user.getId());
                LoginResponse loginResponse = new LoginResponse(
                        user.getId(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getProfileId(),
                        userStatusRepository.isOnline(userStatus.getId())
                );
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(loginResponse);
            }
        }
        throw new RuntimeException("로그인 시도. 일치하는 유저 없습니다.");
    }
}
