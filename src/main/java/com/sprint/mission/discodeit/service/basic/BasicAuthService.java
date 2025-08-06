package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.userException.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.jpa.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic fileName       : BasicAuthService
 * author         : doungukkim date           : 2025. 4. 25. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 25.        doungukkim 최초 생성
 */

@Slf4j
@Primary
@RequiredArgsConstructor
@Service("basicAuthService")
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getCurrentUserInfo(UserDetails userDetails) {
        if(userDetails == null) {
            log.warn("[AuthController] 유저 인증 실패");
            throw new UserNotFoundException();
        }

        String username = userDetails.getUsername();
        log.info("[AuthService] 조회할 사용자 이름: " + username);
        User currentUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        return userMapper.toDto(currentUser);
    }

    //    public LoginResponse login(LoginRequest request) {
//
//        String username = request.username();
//        String password = request.password();
//
//        User user = userRepository.findByUsernameWithProfileAndStatus(username).orElseThrow(() -> new NoSuchElementException("User with username " + username + " not found"));
//
//        BinaryContent profile = user.getProfile();
//        BinaryContentResponse profileDto = null;
//        if (profile != null) {
//            profileDto = new BinaryContentResponse(
//                profile.getId(),
//                profile.getFileName(),
//                profile.getSize(),
//                profile.getContentType()
//            );
//        }
//
//        if (user.getPassword().equals(password)) {
//            LoginResponse loginResponse = new LoginResponse(
//                user.getId(),
//                user.getUsername(),
//                user.getEmail(),
//                profileDto,
//                isOnline(user.getStatus())
//            );
//            return loginResponse;
//        }
//        throw new IllegalArgumentException("wrong password");
//    }


    private static boolean isOnline(UserStatus userStatus) {
        Instant now = Instant.now();
        return Duration.between(userStatus.getLastActiveAt(), now).toMinutes() < 5;
    }
}


