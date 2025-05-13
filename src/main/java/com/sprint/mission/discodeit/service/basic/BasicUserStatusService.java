package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
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

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest userStatusCreateRequest) {
        // 존재여부만 확인
        userRepository.findById(userStatusCreateRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + userStatusCreateRequest.getUserId()));

        // Create
        Instant lastOnline = userStatusCreateRequest.getLastOnlineAt();
        UserStatus userStatus = new UserStatus(userStatusCreateRequest.getUserId(), lastOnline);

        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID id, UserStatusUpdateRequest userStatusUpdateRequest) {
        Instant newLastOnline = userStatusUpdateRequest.getLastOnlineAt();

        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));

        userStatus.update(newLastOnline);

        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateRequest) {
        Instant newLastOnlineAt = userStatusUpdateRequest.getLastOnlineAt();

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
        userStatus.update(newLastOnlineAt);

        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("UserStatus with id " + id + " not found");
        }
        userStatusRepository.deleteById(id);
    }
}
