package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final SessionRegistry sessionRegistry;
    private final UserRepository userRepository;
    private final BinaryContentMapper binaryContentMapper;

    public UserDto getCurrentUserInfo(UserDetails userDetails) {
        log.info("현재 사용자 정보 조회 요청");

        if (userDetails == null) {
            log.debug("UserDetails가 null입니다.");
            return null;
        }

        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

        log.debug("조회된 사용자 정보: {}", user);

        UserDto userDto = new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            binaryContentMapper.toDto(user.getProfile()),
            isLoggedIn(user.getId()),
            user.getRole()
        );

        return userDto;
    }

    public boolean isLoggedIn(UUID userId) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof DiscodeitUserDetails p && p.getUserDto().id().equals(userId)) {
                return !sessionRegistry.getAllSessions(principal, false).isEmpty();
            }
        }
        return false;
    }
}