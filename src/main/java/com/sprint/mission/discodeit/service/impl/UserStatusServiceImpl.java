package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService {

    private final UserStatusRepository userStatusRepository;

    @Override
    public UserStatus updateStatus(UUID userId, String status) {
        UserStatus newStatus = new UserStatus(
                userId,
                status,
                Instant.now()
        );
        return userStatusRepository.save(newStatus);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId);
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        userStatusRepository.deleteByUserId(userId);
    }
}