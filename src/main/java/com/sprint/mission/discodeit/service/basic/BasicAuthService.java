package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User login(String username, String password) {
        User user = userRepository.loadByName(username);

        if (user == null) {
            throw new IllegalArgumentException("[Auth] 유효하지 않은 사용자입니다. (username: " + username + ")");
        }

        if (!user.authenticate(password)) {
            throw new IllegalArgumentException("[Auth] 비밀번호가 일치하지 않습니다.");
        }

        return user;
    }


    @Override
    public UserStatus updateUserStatus(UUID userId) {
        UserStatus userStatus = userStatusRepository
                .loadById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("[Auth] 유효하지 않은 UserStatus. (userId=" + userId + ")")
                );

        if (userStatus == null) {
            throw new IllegalArgumentException("[Auth] 유효하지 않은 UserStatus (userId: " + userId + ")");
        }

        userStatus.update(Instant.now());
        return userStatusRepository.save(userStatus);
    }
}