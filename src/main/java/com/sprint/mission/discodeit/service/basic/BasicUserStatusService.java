package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Transactional
    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        UUID userId = request.userId();

        log.info("사용자 상태 생성 중 - userId: {}, lastActiveAt: {}", userId, request.lastActiveAt());

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("사용자 상태 생성 실패 - 존재하지 않는 사용자 ID: {}", userId);
                return new UserNotFoundException(userId);
            });
        Optional.ofNullable(user.getStatus())
            .ifPresent(status -> {
                log.warn("사용자 상태 생성 실패 - 이미 상태가 존재함 (userStatusId: {})", status.getId());
                throw new UserStatusAlreadyExistsException(status.getId());
            });

        Instant lastActiveAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(user, lastActiveAt);
        userStatusRepository.save(userStatus);

        log.info("사용자 상태 생성 완료 - userStatusId: {}, userId: {}",
            userStatus.getId(), userId);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
            .map(userStatusMapper::toDto)
            .orElseThrow(
                () -> UserStatusNotFoundException.byUserId(userStatusId));
    }

    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
            .map(userStatusMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        log.info("사용자 상태 수정 중 - userStatusId: {}, newLastActiveAt: {}",
            userStatusId, newLastActiveAt);

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> {
                log.warn("사용자 상태 수정 실패 - 존재하지 않는 userStatusId: {}", userStatusId);
                return UserStatusNotFoundException.byUserStatusId(userStatusId);
            });
        userStatus.update(newLastActiveAt);

        log.info("사용자 상태 수정 중 - userStatusId: {}, newLastActiveAt: {}",
            userStatusId, newLastActiveAt);

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        log.info("사용자 ID로 상태 수정 중 - userId: {}, newLastActiveAt: {}",
            userId, newLastActiveAt);

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> {
                log.warn("사용자 상태 수정 실패 - 존재하지 않는 userId: {}", userId);
                return UserStatusNotFoundException.byUserId(userId);
            });
        userStatus.update(newLastActiveAt);

        log.info("사용자 ID로 상태 수정 중 - userId: {}, newLastActiveAt: {}",
            userId, newLastActiveAt);

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public void delete(UUID userStatusId) {
        log.info("사용자 상태 삭제 시작 - userStatusId: {}", userStatusId);

        if (!userStatusRepository.existsById(userStatusId)) {
            log.warn("사용자 상태 삭제 실패 - 존재하지 않는 userStatusId: {}", userStatusId);
            throw UserStatusNotFoundException.byUserStatusId(userStatusId);
        }
        userStatusRepository.deleteById(userStatusId);

        log.info("사용자 상태 삭제 시작 - userStatusId: {}", userStatusId);
    }
}
