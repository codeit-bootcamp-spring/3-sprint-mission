package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    public final UserRepository userRepository;
    public final UserStatusRepository userStatusRepository;
    public final UserStatusMapper userStatusMapper;

    @Transactional
    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        UUID userId = request.userId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("User", ErrorMessages.ERROR_NOT_FOUND)
            ));

        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException(
                ErrorMessages.format("UserStatus", ErrorMessages.ERROR_EXISTS));
        }

        Instant lastOnlineTime = request.lastActiveAt();
        UserStatus status = new UserStatus(user, lastOnlineTime);

        //양방향 연관관계 명확히 하기위해
        user.setUserStatus(status);
        UserStatus savedStatus = userStatusRepository.save(status);

        return userStatusMapper.toDto(savedStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public UserStatusDto find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("UserStatus", ErrorMessages.ERROR_NOT_FOUND)));

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserStatusDto> findAll() {
        List<UserStatus> userStatuses = userStatusRepository.findAll();

        return userStatuses.stream()
            .map(userStatusMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("UserStatus", ErrorMessages.ERROR_NOT_FOUND)));

        userStatus.update(newLastActiveAt);

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(
                () -> new NoSuchElementException(
                    ErrorMessages.format("UserStatus", ErrorMessages.ERROR_NOT_FOUND)));
        userStatus.update(newLastActiveAt);

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException(
                ErrorMessages.format("UserStatus", ErrorMessages.ERROR_NOT_FOUND));
        }
        userStatusRepository.deleteById(id);
    }
}
