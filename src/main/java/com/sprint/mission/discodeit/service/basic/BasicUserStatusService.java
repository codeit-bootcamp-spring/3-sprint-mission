package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userStatus.DuplicateUserStatusException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Transactional
    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new UserNotFoundException(request.userId()));

        if (userStatusRepository.findByUserId(user.getId()).isPresent()) {
            throw new DuplicateUserStatusException(user.getUserStatus().getId());
        }

        Instant lastActiveAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(user, lastActiveAt);
        return userStatusRepository.save(userStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId).orElseThrow(() -> new UserNotFoundException(userStatusId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Transactional
    @Override
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {

        UserStatus userStatus = userStatusRepository.findById(userStatusId).orElseThrow(() -> new UserNotFoundException(userStatusId));
        userStatus.update(request.newLastActiveAt());

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {

        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(userId));
        userStatus.update(request.newLastActiveAt());

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new UserStatusNotFoundException(userStatusId);
        }
        userStatusRepository.deleteById(userStatusId);
    }
}
