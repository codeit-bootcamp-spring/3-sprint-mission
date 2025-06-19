package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Transactional
    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        log.info("사용자 상태 생성 요청: userId={}, lastActiveAt={}", request.userId(),
            request.lastActiveAt());

        UUID userId = request.userId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("UserStatus 생성 실패: 존재하지 않는 사용자 ID={}", userId);
                return new NoSuchElementException("User with id " + userId + " not found");
            });

        Optional.ofNullable(user.getStatus()).ifPresent(status -> {
            log.error("UserStatus 중복 생성 시도: userId={}", userId);
            throw new IllegalArgumentException("UserStatus with id " + userId + " already exists");
        });

        Instant lastActiveAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(user, lastActiveAt);
        userStatusRepository.save(userStatus);

        log.info("UserStatus 생성 완료: id={}", userStatus.getId());
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto find(UUID userStatusId) {
        log.debug("UserStatus 조회 요청: id={}", userStatusId);

        return userStatusRepository.findById(userStatusId)
            .map(userStatusMapper::toDto)
            .orElseThrow(() -> {
                log.warn("UserStatus 조회 실패: 존재하지 않는 ID={}", userStatusId);
                return new NoSuchElementException(
                    "UserStatus with id " + userStatusId + " not found");
            });
    }

    @Override
    public List<UserStatusDto> findAll() {
        log.debug("모든 UserStatus 조회 요청");
        return userStatusRepository.findAll().stream()
            .map(userStatusMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
        log.info("UserStatus 수정 요청: id={}, newLastActiveAt={}", userStatusId,
            request.newLastActiveAt());

        Instant newLastActiveAt = request.newLastActiveAt();
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> {
                log.warn("UserStatus 수정 실패: 존재하지 않는 ID={}", userStatusId);
                return new NoSuchElementException(
                    "UserStatus with id " + userStatusId + " not found");
            });

        userStatus.update(newLastActiveAt);
        log.info("UserStatus 수정 완료: id={}", userStatusId);
        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        log.info("UserStatus 사용자 ID 기준 수정 요청: userId={}, newLastActiveAt={}", userId,
            request.newLastActiveAt());

        Instant newLastActiveAt = request.newLastActiveAt();
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> {
                log.warn("UserStatus 수정 실패: userId={} 기준 상태 없음", userId);
                return new NoSuchElementException(
                    "UserStatus with userId " + userId + " not found");
            });

        userStatus.update(newLastActiveAt);
        log.info("UserStatus 수정 완료: userId={}", userId);
        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public void delete(UUID userStatusId) {
        log.info("UserStatus 삭제 요청: id={}", userStatusId);

        if (!userStatusRepository.existsById(userStatusId)) {
            log.error("UserStatus 삭제 실패: 존재하지 않는 ID={}", userStatusId);
            throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
        }

        userStatusRepository.deleteById(userStatusId);
        log.info("UserStatus 삭제 완료: id={}", userStatusId);
    }
}