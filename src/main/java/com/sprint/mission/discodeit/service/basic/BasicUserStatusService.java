package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Transactional(readOnly = true)
    @Override
    public UserStatusResponse find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
            .orElseThrow(
                () -> new NoSuchElementException("UserStatus with id " + id + " not found"));
        return userStatusMapper.toResponse(userStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
            .map(userStatusMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new NoSuchElementException("User with id " + userId + " does not exist"));

        if (userStatusRepository.findByUser_Id(userId).isPresent()) {
            throw new IllegalArgumentException(
                "UserStatus for userId " + userId + " already exists");
        }

        UserStatus userStatus = new UserStatus(user, request.lastActiveAt());
        return userStatusMapper.toResponse(userStatus);

    }

    @Transactional
    @Override
    public UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new NoSuchElementException(
                "UserStatus with id " + userStatusId + " not found"));

        userStatus.update(request.newLastActiveAt());
        return userStatusMapper.toResponse(userStatus);
    }

    @Transactional
    @Override
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastAccessedAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
            .orElseThrow(
                () -> new NoSuchElementException(
                    "UserStatus with userId " + userId + " not found"));
        userStatus.update(newLastAccessedAt);

        return userStatusMapper.toResponse(userStatus);
    }

    @Transactional
    @Override
    public UserStatusResponse delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
            .orElseThrow(
                () -> new NoSuchElementException("UserStatus with id " + id + " not found"));

        userStatusRepository.deleteById(id);
        return userStatusMapper.toResponse(userStatus);
    }
}
