package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest userStatusCreateRequest) {
        UUID userId = userStatusCreateRequest.getUserId();

        User user = userRepository.loadById(userId);

        if (user == null) {
            throw new IllegalArgumentException("[UserStatus] 존재하지 않은 사용자입니다. (userId=" + userId + ")");
        }

        UserStatus userStatus =  UserStatus.of(userId);
        userStatusRepository.save(userStatus);

        return userStatus;
    }

    @Override
    public UserStatus find(UUID userId) {
        return userStatusRepository.loadById(userId)
                .orElseThrow(
                        () -> new NoSuchElementException("[UserStatus] 존재하지 않은 사용자입니다. (userId=" + userId + ")"));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.loadAll();
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateRequest) {
        Instant newUpdateaAT = userStatusUpdateRequest.newUpdateaAT();

        UserStatus userStatus = userStatusRepository.loadById(userId)
                .orElseThrow(
                        () -> new NoSuchElementException("[UserStatus] 존재하지 않은 사용자입니다. (userId=" + userId + ")"));
        userStatus.update(newUpdateaAT);

        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteByUserId(id);
    }
}
