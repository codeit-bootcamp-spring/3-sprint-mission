package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Dto.authService.LoginRequest;
import com.sprint.mission.discodeit.Dto.authService.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileAuthService
 * author         : doungukkim
 * date           : 2025. 4. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 25.        doungukkim       최초 생성
 */
@RequiredArgsConstructor
@Service
@Profile("file")
public class FileAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;


    public LoginResponse login(LoginRequest request) {
        Objects.requireNonNull(request, "입력 값 없음");

        String username = Optional.ofNullable(request.username())
                .orElseThrow(() -> new IllegalArgumentException("no name"));

        String password = Optional.ofNullable(request.password())
                .orElseThrow(() -> new IllegalArgumentException("no password"));

        List<User> allUsers = userRepository.findAllUsers();
        for (User user : allUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                UserStatus userStatus = userStatusRepository.findUserStatusByUserId(user.getId());
                return new LoginResponse(
                        user.getId(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getProfileId(),
                        userStatusRepository.isOnline(userStatus.getId())
                );
            }
        }
        throw new RuntimeException("로그인 시도. 일치하는 유저 없습니다.");
    }
}
