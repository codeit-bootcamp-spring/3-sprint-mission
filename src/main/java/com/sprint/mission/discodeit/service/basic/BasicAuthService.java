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
    private final UserStatusRepository userStatusRepository; //이후에 login 로직에서 사용

    // 수정해야될 로직 (user가 직접 password를 판단)
    @Override
    public UserStatus login(UUID userId, String password) {
        User user = userRepository.loadById(userId);
        if (user == null) {
            throw new IllegalArgumentException("[Auth] 유효하지 않은 사용자입니다. (userID: " + userId + ")");
        }

        validatePassword(user, password);
        UserStatus userStatus =  updateUserStatus(user.getId());

        return userStatus;
    }

    @Override
    public void validatePassword(User user, String password) {
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("[Auth] 패스워드가 일치하지 않습니다. (password: " + password + ")");
        }
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