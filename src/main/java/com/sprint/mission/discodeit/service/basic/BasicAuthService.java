package com.sprint.mission.discodeit.service.basic;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.auth.AuthRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusService userStatusService;

    @Override
    public User login(AuthRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.username())
                .filter(u -> u.getPassword().equals(loginRequest.password()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        try {
            UserStatusUpdateRequest statusUpdateRequest = new UserStatusUpdateRequest(LocalDateTime.now());
            userStatusService.updateByUserId(user.getUserId(), statusUpdateRequest);
        } catch (Exception e) {
            System.err.println("Failed to update user status for user " + user.getUserId() + ": " + e.getMessage());
        }

        return user;
    }
}
