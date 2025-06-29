package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.userstatus.DuplicatedUserStatusException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public UserStatus create(UserStatusCreateRequest request) {
        UUID userId = request.userId();

        if (!userRepository.existsById(userId)) {
            throw new UserStatusNotFoundException("사용자를 찾을 수 없습니다.");
        }
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new DuplicatedUserStatusException("UserStatus with id " + userId + " already exists");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserStatusNotFoundException("사용자를 찾을 수 없습니다."));

        Instant lastActiveAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(user, lastActiveAt);
        return userStatusRepository.save(userStatus);
    }

    @Override
    @Transactional(readOnly = true,
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new UserStatusNotFoundException(
                "유저 상태 정보를 찾을 수 없습니다."));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll().stream()
            .toList();
    }

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new UserStatusNotFoundException(
                "유저 상태 정보를 찾을 수 없습니다."));
        userStatus.update(newLastActiveAt);

        return userStatusRepository.save(userStatus);
    }

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new UserStatusNotFoundException(
                "유저 상태 정보를 찾을 수 없습니다."));
        userStatus.update(newLastActiveAt);

        UserStatusDto userStatusDto = userStatusMapper.toDto(userStatus);
        userStatusRepository.save(userStatus);

        return userStatusDto;
    }

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new UserStatusNotFoundException("유저 상태 정보를 찾을 수 없습니다.");
        }
        userStatusRepository.deleteById(userStatusId);
    }
}
