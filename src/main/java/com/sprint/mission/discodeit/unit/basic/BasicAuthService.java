package com.sprint.mission.discodeit.unit.basic;

import com.sprint.mission.discodeit.dto.authService.LoginRequest;
import com.sprint.mission.discodeit.dto.authService.LoginResponse;
import com.sprint.mission.discodeit.dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.unit.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.NoSuchElementException;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic fileName       : BasicAuthService
 * author         : doungukkim date           : 2025. 4. 25. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 25.        doungukkim 최초 생성
 */

@Primary
@RequiredArgsConstructor
@Service("basicAuthService")
public class BasicAuthService implements AuthService {
    private final JpaUserRepository userRepository;

    public LoginResponse login(LoginRequest request) {

        String username = request.username();
        String password = request.password();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User with username " + username + " not found"));

        BinaryContent profile = user.getProfile();
        JpaBinaryContentResponse profileDto = null;
        if(profile != null) {
            profileDto = new JpaBinaryContentResponse(
                    profile.getId(),
                    profile.getFileName(),
                    profile.getSize(),
                    profile.getContentType()
            );
        }

        if(user.getPassword().equals(password)) {
            LoginResponse loginResponse = new LoginResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    profileDto,
                    isOnline(user.getStatus())
            );
            return loginResponse;
        }
        throw new IllegalArgumentException("wrong password");
    }
    
    private static boolean isOnline(UserStatus userStatus) {
        Instant now = Instant.now();
        return Duration.between(userStatus.getLastActiveAt(), now).toMinutes() < 5;
    }
}
