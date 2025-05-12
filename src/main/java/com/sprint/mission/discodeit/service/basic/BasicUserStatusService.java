package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + request.userId()));

        Optional<UserStatus> existingStatus = userStatusRepository.findByUserId(request.userId());
        if (existingStatus.isPresent()) {
            throw new IllegalStateException("UserStatus already exists for user id: " + request.userId());
        }

        UserStatus newUserStatus = new UserStatus(request.userId(), request.lastActiveAt());

        return userStatusRepository.save(newUserStatus);
    }

    @Override
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id: " + userStatusId));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Transactional
    @Override
    public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
        UserStatus userStatus = find(userStatusId);

        userStatus.update(request.newLastActiveAt());

        return userStatusRepository.save(userStatus);
    }

    @Transactional
    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId + ". Cannot update UserStatus."));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for user id: " + userId));

        userStatus.update(request.newLastActiveAt());

        return userStatusRepository.save(userStatus);
    }

    @Transactional
    @Override
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new NoSuchElementException("UserStatus not found with id: " + userStatusId + ". Cannot delete.");
        }

        userStatusRepository.deleteById(userStatusId);
    }
}
