package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Dto.authService.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
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
//@ConditionalOnProperty(name = "service.mode", havingValue = "file")
public class FileAuthService implements AuthService {
    private final UserRepository userRepository;


    public User login(LoginRequest request) {
        Objects.requireNonNull(request, "입력 값 없음");

        String username = Optional.ofNullable(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("no name"));

        String password = Optional.ofNullable(request.getPassword())
                .orElseThrow(() -> new IllegalArgumentException("no password"));

        List<User> allUsers = userRepository.findAllUsers();
        for (User user : allUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new RuntimeException("로그인 시도. 일치하는 유저 없습니다.");
    }
}
